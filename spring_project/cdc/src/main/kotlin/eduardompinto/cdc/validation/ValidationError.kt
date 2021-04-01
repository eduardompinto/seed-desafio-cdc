package eduardompinto.cdc.validation

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

data class ValidationError(val errors: List<Violation>) {

    data class Violation(val field: String, val message: String) {
        constructor(error: FieldError) : this(
            field = error.field,
            message = error.defaultMessage ?: "Unknown reason"
        )
    }

    companion object {
        fun fromException(ex: Exception): ValidationError =
            when (ex) {
                is MethodArgumentNotValidException -> ValidationError(ex.fieldErrors.map(::Violation))
                is MissingKotlinParameterException -> ValidationError(
                    ex.path.map { Violation(it.fieldName, "must be not null") }
                )
                else -> ValidationError(emptyList())
            }
    }
}
