package eduardompinto.cdc.author

import eduardompinto.cdc.extensions.build
import eduardompinto.cdc.extensions.okOrNotFound
import eduardompinto.cdc.validation.UniqueFieldValidator
import eduardompinto.cdc.validation.UniqueFieldValidator.Companion.build
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
import javax.validation.Valid

/**
 * 5 points
 *
 * 1 - AuthorRepository
 * 2,3 - UniqueFieldValidator / Predicate
 * 4 - AuthorRequest
 * 5 - Author
 */
@RestController
@Validated
class AuthorController(private val repository: AuthorRepository) {

    @InitBinder
    fun init(binder: WebDataBinder) {
        val uniqueEmailValidator: UniqueFieldValidator<AuthorRequest> = build(
            fieldName = "email",
            predicate = { repository.existsByEmail(it.email) }
        )
        binder.addValidators(uniqueEmailValidator)
    }

    @PostMapping("/authors/", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveAuthor(@RequestBody @Valid authorReq: AuthorRequest): ResponseEntity<Author> {
        return authorReq.asAuthor().run(repository::save).run(OK::build)
    }

    @GetMapping("/authors/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAuthor(@PathVariable id: Long): ResponseEntity<Author> {
        return repository.findById(id).okOrNotFound()
    }
}
