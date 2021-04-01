package eduardompinto.cdc.controller

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import eduardompinto.cdc.controller.ValidationError.Violation.Companion.nullViolation
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationErrorAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidValues(e: MethodArgumentNotValidException): ValidationError {
        val fieldErrors = e.bindingResult.fieldErrors
        return fieldErrors
            .map(ValidationError::Violation)
            .run(::ValidationError)
    }

    @ExceptionHandler(MissingKotlinParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingKotlinParameter(exception: MissingKotlinParameterException): ValidationError {
        return ValidationError(exception.path.map { nullViolation(it.fieldName) })
    }

}

data class ValidationError(val errors: List<Violation>) {
    data class Violation(val field: String, val message: String) {
        constructor(error: FieldError) : this(
            field = error.field,
            message = error.defaultMessage ?: "Unknown reason"
        )

        companion object {
            fun nullViolation(field: String) = Violation(field, "must not be null")
        }
    }
}
