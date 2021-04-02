package eduardompinto.cdc.book

import eduardompinto.cdc.author.Author
import eduardompinto.cdc.author.AuthorRepository
import eduardompinto.cdc.category.Category
import eduardompinto.cdc.category.CategoryRepository
import eduardompinto.cdc.extensions.build
import eduardompinto.cdc.extensions.okOrNotFound
import eduardompinto.cdc.validation.ForeignKeyExistsFieldValidator
import eduardompinto.cdc.validation.UniqueFieldValidator
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.Optional
import javax.validation.Valid

@RestController
class BookController(
    private val bookRepository: BookRepository,
    categoryRepository: CategoryRepository,
    authorRepository: AuthorRepository,
) {
    private val getAuthor: (Long) -> Optional<Author> = authorRepository::findById
    private val getCategory: (Long) -> Optional<Category> = categoryRepository::findById

    private val uniqueTitle = UniqueFieldValidator.build<BookRequest>(
        fieldName = "title",
        predicate = { bookRepository.existsByTitle(it.title) }
    )
    private val categoryExists = ForeignKeyExistsFieldValidator.build<BookRequest>(
        fieldName = "category",
        predicate = { getCategory(it.category).isEmpty }
    )
    private val authorExists = ForeignKeyExistsFieldValidator.build<BookRequest>(
        fieldName = "author",
        predicate = { getAuthor(it.author).isEmpty }
    )

    @InitBinder
    fun init(binder: WebDataBinder) {
        binder.addValidators(
            uniqueTitle,
            categoryExists,
            authorExists
        )
    }

    @PostMapping("/books/")
    fun saveBook(@Valid @RequestBody req: BookRequest): ResponseEntity<Book> {
        val author = getAuthor(req.author).get()
        val category = getCategory(req.category).get()
        return req.asBook(author, category).run(bookRepository::save).run(OK::build)
    }

    @GetMapping("/books/{id}")
    fun getBook(@PathVariable id: Long): ResponseEntity<Book> {
        return bookRepository.findById(id).okOrNotFound()
    }
}
