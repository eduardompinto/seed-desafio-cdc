package eduardompinto.author

import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset

object Repository {
    suspend fun create(author: Author): Int =
        dbQuery {
            AuthorTable.insert { statement ->
                statement[name] = author.name
                statement[description] = author.description
                statement[email] = author.email
                statement[createdAt] = LocalDateTime.ofInstant(author.createdAt, ZoneOffset.UTC)
            }[AuthorTable.id].value
        }

    suspend fun read(id: Int): Author? {
        return dbQuery {
            AuthorTable.selectAll().where { AuthorTable.id eq id }
                .map { row ->
                    Author(
                        name = row[AuthorTable.name],
                        description = row[AuthorTable.description],
                        email = row[AuthorTable.email],
                        createdAt = row[AuthorTable.createdAt].toInstant(ZoneOffset.UTC),
                    )
                }
                .singleOrNull()
        }
    }
}
