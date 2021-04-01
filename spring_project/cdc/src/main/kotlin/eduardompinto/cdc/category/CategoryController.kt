package eduardompinto.cdc.category

import eduardompinto.cdc.extensions.build
import org.springframework.http.HttpStatus.NOT_FOUND
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
class CategoryController(
    private val repository: CategoryRepository,
    private val blockDuplicatedCategoryValidator: BlockDuplicatedCategoryValidator
) {

    @InitBinder
    fun init(binder: WebDataBinder) {
        binder.addValidators(blockDuplicatedCategoryValidator)
    }

    @PostMapping("/categories/")
    fun saveCategory(@Valid @RequestBody req: CategoryRequest): ResponseEntity<Category> {
        return req.asCategory().save().run(OK::build)
    }

    @GetMapping("/categories/{id}")
    fun getCategory(@PathVariable id: Long): ResponseEntity<Category> {
        val category: Optional<Category> = repository.findById(id)
        return when {
            category.isPresent -> category.get().run(OK::build)
            else -> NOT_FOUND.build()
        }
    }

    private fun Category.save() = repository.save(this)
}
