package eduardompinto.cdc.controller.validators

import eduardompinto.cdc.controller.AuthorRequest
import eduardompinto.cdc.repository.AuthorRepository
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * 4 points
 *
 * 1 - AuthorRepository
 * 2 - validate if has errors
 * 3 - check target type
 * 4 - if exists by email
 */
@Component
class BlockDuplicatedAuthorValidator(
    private val authorRepository: AuthorRepository,
) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return AuthorRequest::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (errors.hasErrors()) {
            return
        }
        check(target is AuthorRequest) {
            "BlockDuplicatedAuthorValidator supports ${AuthorRequest::class.qualifiedName}," +
                " received ${target::class.qualifiedName}"
        }

        if (authorRepository.existsByEmail(target.email)) {
            errors.rejectValue("email", "email_already_taken", "email already registered on the platform")
        }
    }
}
