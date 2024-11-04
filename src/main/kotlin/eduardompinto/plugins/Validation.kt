package eduardompinto.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberFunctions

fun Application.configureValidation() {
    install(RequestValidation) {
        validate {
            filter { it::class.findAnnotations<ValidRequest>().isNotEmpty() }
            validation {
                customValidator(it) + validateUniqueStringField(it) + validateNotBlank(it)
            }
        }
    }
}

suspend fun validateRowExist(
    table: IntIdTable,
    id: Int,
): Boolean {
    return dbQuery {
        table.selectAll().where { table.id eq id }.any()
    }
}

private suspend fun validateUniqueStringField(model: Any): ValidationResult {
    val fields = getFields<UniqueStringField>(model)
    val invalidFields = checkAlreadyPresentField<String>(fields, model)

    return when {
        invalidFields.isEmpty() -> ValidationResult.Valid
        else -> ValidationResult.Invalid("DUPLICATED_FIELDS: ${invalidFields.joinToString()}")
    }
}

private fun validateNotBlank(model: Any): ValidationResult {
    val fields = getFields<NotBlank>(model)
    val reasons =
        fields.mapNotNull {
            val value = it.get(model) as String
            "${it.name.capitalize()} cannot be blank".takeIf { value.isBlank() }
        }
    return if (reasons.isEmpty()) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid(reasons)
    }
}

private suspend fun <K> checkAlreadyPresentField(
    fields: List<Field>,
    model: Any,
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

private inline fun <reified A : Annotation> getFields(model: Any): List<Field> =
    model::class.java.declaredFields.filter { it.isAnnotationPresent(A::class.java) }.mapNotNull {
        it.isAccessible = true
        it
    }

private suspend fun customValidator(any: Any): ValidationResult {
    return when (any) {
        is Validatable -> any.validate()
        else -> {
            // Looks for validate function and tries to call it.
            // Bad stuff, but it's a toy project, and it was something nice to learn
            val validateFunction =
                any::class.memberFunctions.find {
                    it.name == "validate"
                } ?: return ValidationResult.Valid

            return validateFunction.callSuspend(any) as ValidationResult
        }
    }
}

private operator fun ValidationResult.plus(other: ValidationResult): ValidationResult {
    return when (this) {
        is ValidationResult.Valid -> other
        is ValidationResult.Invalid -> {
            val reasons =
                when (other) {
                    is ValidationResult.Invalid -> this.reasons + other.reasons
                    else -> this.reasons
                }
            ValidationResult.Invalid(reasons.joinToString("\n"))
        }
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class UniqueStringField(val table: KClass<out Table>, val columnName: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class NotBlank

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ValidRequest

interface Validatable {
    suspend fun validate(): ValidationResult
}
