package eduardompinto.cdc.book

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : CrudRepository<Book, Long> {
    fun existsByTitle(title: String): Boolean
}
