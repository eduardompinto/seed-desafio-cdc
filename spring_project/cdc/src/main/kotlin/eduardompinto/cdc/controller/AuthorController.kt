package eduardompinto.cdc.controller

import eduardompinto.cdc.model.Author
import eduardompinto.cdc.repository.AuthorRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.Optional
import javax.validation.Valid

@RestController
@Validated
class AuthorController(private val repository: AuthorRepository) {

    @PostMapping("/authors/", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveAuthor(@RequestBody @Valid authorReq: AuthorRequest): ResponseEntity<Author> {
        return when {
            repository.existsByEmail(authorReq.email) -> CONFLICT.build()
            else -> OK.build(authorReq.asAuthor().save())
        }
    }

    @GetMapping("/authors/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAuthor(@PathVariable id: Long): ResponseEntity<Author> {
        val author: Optional<Author> = repository.findById(id)
        return when {
            author.isPresent -> OK.build(author.get())
            else -> NOT_FOUND.build()
        }
    }

    private fun <T> HttpStatus.build(t: T? = null): ResponseEntity<T> =
        ResponseEntity.status(this).body(t)

    private fun Author.save(): Author = run(repository::save)
}
