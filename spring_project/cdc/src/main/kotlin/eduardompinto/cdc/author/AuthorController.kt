package eduardompinto.cdc.author

import eduardompinto.cdc.extensions.build
import eduardompinto.cdc.validation.UniqueFieldValidator
import eduardompinto.cdc.validation.buildUniqueFieldValidator
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.Optional
import javax.validation.Valid

/**
 * 7 points
 *
 * 1 - AuthorRepository
 * 2,3 - UniqueFieldValidator / Predicate
 * 4 - AuthorRequest
 * 5 - Author
 * 6,7 - getAuthor
 */
@RestController
@Validated
class AuthorController(private val repository: AuthorRepository) {

    @InitBinder
    fun init(binder: WebDataBinder) {
        val uniqueEmailValidator: UniqueFieldValidator<AuthorRequest> = buildUniqueFieldValidator(
            fieldName = "email",
            predicate = { repository.existsByEmail(it.email) }
        )
        binder.addValidators(uniqueEmailValidator)
    }

    @PostMapping("/authors/", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveAuthor(@RequestBody @Valid authorReq: AuthorRequest): ResponseEntity<Author> {
        return authorReq.asAuthor().save().run(OK::build)
    }

    @GetMapping("/authors/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAuthor(@PathVariable id: Long): ResponseEntity<Author> {
        val author: Optional<Author> = repository.findById(id)
        return when {
            author.isPresent -> author.get().run(OK::build)
            else -> NOT_FOUND.build()
        }
    }

    private fun Author.save(): Author = run(repository::save)
}
