package eduardompinto.plugins

import eduardompinto.author.AuthorRequest
import eduardompinto.category.CategoryRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import java.lang.reflect.Field
import kotlin.reflect.KClass

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<AuthorRequest> { it.validate() }
        validate<CategoryRequest> { it.validate() }
        validate {
            filter { true }
            validation { validateUniqueStringField(it) }
        }
    }
}

private val emailRegex =
    "^(?=.{1,256})(?=.{1,64}@.{1,255}$)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$".toRegex(
        RegexOption.IGNORE_CASE,
    )

fun isValidEmail(email: String): Boolean {
    return email.matches(emailRegex)
}

private suspend fun <T> validateUniqueStringField(model: T): ValidationResult {
    val fields = getFields<T, UniqueStringField>(model)
    val invalidFields = checkAlreadyPresentField<T, String>(fields, model)

    return when {
        invalidFields.isEmpty() -> ValidationResult.Valid
        else -> ValidationResult.Invalid("DUPLICATED_FIELDS: ${invalidFields.joinToString()}")
    }
}

private suspend fun <T, K> checkAlreadyPresentField(
    fields: List<Field>,
    model: T,
): List<String> {
    val invalidFields =
        fields.mapNotNull { field ->
            val annotation = field.getAnnotation(UniqueStringField::class.java)
            val value = field.get(model) as K
            val table = annotation.table.objectInstance!!
            val column =
                table.columns.find {
                    it.name == annotation.columnName
                } as Column<K>
            val isUnique =
                dbQuery {
                    table.selectAll().where { column eq value }.count() == 0L
                }
            field.name.takeUnless { isUnique }
        }
    return invalidFields
}

private inline fun <T, reified A : Annotation> getFields(model: T): List<Field> =
    model!!::class.java.declaredFields.filter { it.isAnnotationPresent(A::class.java) }.mapNotNull {
        it.isAccessible = true
        it
    }

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class UniqueStringField(val table: KClass<out Table>, val columnName: String)

// Another possible approach to validate unique fields

// suspend fun <R, C> validateUnique(request: R, validation: UniqueValidation<C>): ValidationResult {
//    checkNotNull(request)
//    val field: KCallable<*> = request::class.declaredMembers.first { it.name == validation.requestField }
//    val value: C = field.call(request) as C
//
//    val isUnique = dbQuery {
//        validation.table.selectAll().where { validation.column eq value }.count() == 0L
//    }
//
//    return when {
//        isUnique -> ValidationResult.Valid
//        else -> ValidationResult.Invalid("Field ${validation.requestField} has to be unique")
//    }
//
// }
