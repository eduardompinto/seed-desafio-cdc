package eduardompinto.cdc.author

import eduardompinto.cdc.extensions.build
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
 * 6 points
 *
 * 1 - AuthorRepository
 * 2 - BlockDuplicatedAuthorValidator
 * 3 - AuthorRequest
 * 4 - Author
 * 6 - getAuthor (when 2 conditions)
 */
@RestController
@Validated
class AuthorController(
    private val repository: AuthorRepository,
    private val blockDuplicatedAuthorValidator: BlockDuplicatedAuthorValidator,
) {

    @InitBinder
    fun init(binder: WebDataBinder) {
        binder.addValidators(blockDuplicatedAuthorValidator)
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
