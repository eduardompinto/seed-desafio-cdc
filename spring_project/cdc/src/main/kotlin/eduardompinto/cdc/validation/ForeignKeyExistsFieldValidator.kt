package eduardompinto.cdc.validation

import java.util.function.Predicate

class ForeignKeyExistsFieldValidator<T>(
    requestClass: Class<T>,
    predicate: Predicate<T>,
    fieldName: String,
) : GenericFieldValidator<T>(
    requestClass = requestClass,
    predicate = predicate,
    fieldName = fieldName,
    errorCodeSuffix = "must_exist",
    messageSuffix = "foreign key doesn't exists",
) {
    companion object {
        inline fun <reified T> build(
            fieldName: String,
            predicate: Predicate<T>,
        ) = ForeignKeyExistsFieldValidator(
            T::class.java,
            predicate,
            fieldName
        )
    }
}
