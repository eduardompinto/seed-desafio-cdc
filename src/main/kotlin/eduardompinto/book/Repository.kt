package eduardompinto.book

import eduardompinto.author.AuthorTable
import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

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

    suspend fun <T> findAll(mapper: (ResultRow) -> T): List<T> {
        return dbQuery {
            BookTable.selectAll().map { row ->
                mapper(row)
            }
        }
    }

    suspend fun <T> findBookWithAuthor(
        id: Int,
        mapper: (ResultRow) -> T,
    ): T? {
        return dbQuery {
            BookTable.join(
                AuthorTable,
                JoinType.LEFT,
                BookTable.authorId,
                AuthorTable.id,
            ).selectAll()
                .where { BookTable.id eq id }
                .firstOrNull()?.let { row ->
                    mapper(row)
                }
        }
    }
}
