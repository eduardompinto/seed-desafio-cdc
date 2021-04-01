package eduardompinto.cdc.validation

import java.util.function.Predicate

open class DuplicatedValidator<T>(
    requestClass: Class<T>,
    testedClass: Class<*>,
    predicate: Predicate<T>,
    fieldName: String,
) : GenericValidator<T>(
    requestClass = requestClass,
    testedClass = testedClass,
    predicate = predicate,
    fieldName = fieldName,
    errorCodeSuffix = "already_taken",
    messageSuffix = "already registered on the platform"
)
