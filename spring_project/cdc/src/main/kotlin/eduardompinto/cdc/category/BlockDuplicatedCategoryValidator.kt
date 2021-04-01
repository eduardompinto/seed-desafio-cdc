package eduardompinto.cdc.category

import eduardompinto.cdc.validation.DuplicatedValidator
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class BlockDuplicatedCategoryValidator(repository: CategoryRepository) :
    DuplicatedValidator<CategoryRequest>(
        requestClass = CategoryRequest::class.java,
        testedClass = Category::class.java,
        predicate = Predicate { repository.existsByName(it.name) },
        fieldName = "name"
    )
