package eduardompinto.cdc.author

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
            "${this::class.simpleName} supports ${AuthorRequest::class.qualifiedName}," +
                " received ${target::class.qualifiedName}"
        }

        if (authorRepository.existsByEmail(target.email)) {
            errors.rejectValue(
                "email",
                "author, email_already_taken",
                "author email already registered on the platform"
            )
        }
    }
}
