package eduardompinto.cdc.author

import eduardompinto.cdc.validation.DuplicatedValidator
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class BlockDuplicatedAuthorValidator(repository: AuthorRepository) : DuplicatedValidator<AuthorRequest>(
    requestClass = AuthorRequest::class.java,
    testedClass = Author::class.java,
    predicate = Predicate { repository.existsByEmail(it.email) },
    fieldName = "email",
)
