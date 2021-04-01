package eduardompinto.cdc.validation

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 1 - Point
 * 1 - Validation error
 */
@RestControllerAdvice
class ValidationErrorAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class, MissingKotlinParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidValues(e: Exception): ValidationError {
        return ValidationError.fromException(e)
    }
}
