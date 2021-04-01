package eduardompinto.cdc.repository

import eduardompinto.cdc.model.Author
import org.springframework.data.repository.CrudRepository

interface AuthorRepository : CrudRepository<Author, Long> {
    fun existsByEmail(email: String): Boolean
}
