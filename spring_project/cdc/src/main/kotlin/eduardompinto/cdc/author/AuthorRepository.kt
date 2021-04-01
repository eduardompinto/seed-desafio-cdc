package eduardompinto.cdc.author

import org.springframework.data.repository.CrudRepository

interface AuthorRepository : CrudRepository<Author, Long> {
    fun existsByEmail(email: String): Boolean
}
