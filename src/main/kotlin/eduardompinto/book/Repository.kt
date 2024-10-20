package eduardompinto.book

import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.insert

object Repository {
    suspend fun create(book: BookInsert): Int =
        dbQuery {
            BookTable.insert { statement ->
                statement[title] = book.title
                statement[summary] = book.summary
                statement[price] = book.price
                statement[pages] = book.pages
                statement[isbn] = book.isbn
                statement[publishedAt] = book.publishedAt
                statement[content] = book.content
                statement[authorId] = book.authorId
                statement[categoryId] = book.categoryId
            }[BookTable.id].value
        }
}
