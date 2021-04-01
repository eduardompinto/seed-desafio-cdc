package eduardompinto.cdc.validation

import java.util.function.Predicate

open class UniqueFieldValidator<T>(
    requestClass: Class<T>,
    predicate: Predicate<T>,
    fieldName: String,
) : GenericValidator<T>(
    requestClass = requestClass,
    predicate = predicate,
    fieldName = fieldName,
    errorCodeSuffix = "already_taken",
    messageSuffix = "already registered on the platform"
)

inline fun <reified T> buildUniqueFieldValidator(fieldName: String, predicate: Predicate<T>):
    UniqueFieldValidator<T> {
        return object : UniqueFieldValidator<T>(
            T::class.java,
            predicate,
            fieldName
        ) {}
    }
