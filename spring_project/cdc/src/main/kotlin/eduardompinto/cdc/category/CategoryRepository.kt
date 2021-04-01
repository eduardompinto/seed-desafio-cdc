package eduardompinto.cdc.category

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CrudRepository<Category, Long> {
    fun existsByName(name: String): Boolean
}
