package eduardompinto.cdc.controller

import org.springframework.validation.FieldError

data class ValidationError(val errors: List<Violation>) {

    constructor(vararg violations: Violation) : this(violations.toList())

    constructor(field: String, message: String) : this(Violation(field, message))

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
