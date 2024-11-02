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

// AI generated code
private val emailRegex =
    "^(?=.{1,256})(?=.{1,64}@.{1,255}$)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,63}$".toRegex(
        RegexOption.IGNORE_CASE,
    )

fun isValidEmail(email: String): Boolean {
    return email.matches(emailRegex)
}

/**
 * AI generated code, I didn't wanted to implement this
 */
fun isValidCPF(candidate: String): Boolean {
    val normalizedCandidate = candidate.replace("[^0-9]".toRegex(), "").substring(0, 11)
    if (normalizedCandidate.length != 11 || normalizedCandidate.all { it == normalizedCandidate[0] }) {
        return false
    }

    fun calculateDigit(digits: String): Int {
        val sum =
            digits.mapIndexed { index, char ->
                val multiplier = 11 - index
                char.toString().toInt() * multiplier
            }.sum()
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }

    val firstDigit = calculateDigit(normalizedCandidate.substring(0, 9))
    if (firstDigit != normalizedCandidate[9].toString().toInt()) {
        return false
    }

    val secondDigit = calculateDigit("${normalizedCandidate.substring(0, 9)}$firstDigit")
    return secondDigit == normalizedCandidate[10].toString().toInt()
}

/**
 * AI generated code, I didn't wanted to implement this
 */
fun isValidCNPJ(candidate: String): Boolean {
    if (candidate.length != 14) return false

    val normalizedCandidate = candidate.replace("[^0-9]".toRegex(), "")

    fun calculateDigit(position: Int): Int {
        val sum =
            (0 until 12).sumOf { i ->
                normalizedCandidate[i + position].toString().toInt() * (13 - i)
            }
        return 11 - (sum % 11) % 11
    }

    val firstDigit = calculateDigit(0)
    if (firstDigit != normalizedCandidate[12].toString().toInt()) {
        return false
    }

    val secondDigit = calculateDigit(1)
    return secondDigit == normalizedCandidate[13].toString().toInt()
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
            // Bad stuff, but it's a toy project and I it was something nice to learn
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
