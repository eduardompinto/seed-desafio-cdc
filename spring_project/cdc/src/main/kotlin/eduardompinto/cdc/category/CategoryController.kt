package eduardompinto.cdc.category

import eduardompinto.cdc.extensions.build
import eduardompinto.cdc.extensions.okOrNotFound
import eduardompinto.cdc.validation.UniqueFieldValidator
import eduardompinto.cdc.validation.UniqueFieldValidator.Companion.build
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class CategoryController(private val repository: CategoryRepository) {

    @InitBinder
    fun init(binder: WebDataBinder) {
        val uniqueName: UniqueFieldValidator<CategoryRequest> = build(
            fieldName = "name",
            predicate = { repository.existsByName(it.name) },
        )
        binder.addValidators(uniqueName)
    }

    @PostMapping("/categories/")
    fun saveCategory(@Valid @RequestBody req: CategoryRequest): ResponseEntity<Category> {
        return req.asCategory().run(repository::save).run(OK::build)
    }

    @GetMapping("/categories/{id}")
    fun getCategory(@PathVariable id: Long): ResponseEntity<Category> {
        return repository.findById(id).okOrNotFound()
    }
}
