package eduardompinto.cdc.category

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * 4 points
 *
 * 1 - CategoryRepository
 * 2 - validate if has errors
 * 3 - check target type
 * 4 - if exists by email
 */
@Component
class BlockDuplicatedCategoryValidator(
    private val repository: CategoryRepository,
) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return CategoryRequest::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        if (errors.hasErrors()) {
            return
        }
        check(target is CategoryRequest) {
            "${this::class.simpleName} supports ${CategoryRequest::class.qualifiedName}," +
                " received ${target::class.qualifiedName}"
        }

        if (repository.existsByName(target.name)) {
            errors.rejectValue("name", "category_name_already_taken", "category name already registered on the platform")
        }
    }
}
