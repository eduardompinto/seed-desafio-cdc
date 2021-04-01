package eduardompinto.cdc.validation

import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.util.function.Predicate

abstract class GenericValidator<T>(
    private val requestClass: Class<T>,
    private val predicate: Predicate<T>,
    private val fieldName: String,
    private val errorCodeSuffix: String,
    private val messageSuffix: String,
) : Validator {
    override fun supports(cls: Class<*>): Boolean {
        return cls.isAssignableFrom(requestClass)
    }

    @Suppress("UNCHECKED_CAST")
    override fun validate(target: Any, errors: Errors) {
        if (errors.hasErrors()) {
            return
        }
        if (predicate.test(target as T)) {
            errors.rejectValue(
                fieldName,
                "${fieldName}_$errorCodeSuffix",
                "$fieldName $messageSuffix"
            )
        }
    }
}
