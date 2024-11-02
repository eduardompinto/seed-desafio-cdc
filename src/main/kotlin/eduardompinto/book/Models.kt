package eduardompinto.book

import eduardompinto.author.Author
import eduardompinto.author.AuthorTable
import eduardompinto.category.Category
import eduardompinto.category.CategoryTable
import eduardompinto.plugins.NotBlank
import eduardompinto.plugins.ValidRequest
import eduardompinto.plugins.Validatable
import eduardompinto.plugins.validateRowExist
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * This is the business model for a book.
 * It shouldn't be exposed to the client.
 */
data class Book(
    val title: String,
    val summary: String,
    val content: String,
    val price: Double,
    val pages: Int,
    val isbn: String,
    val publishedAt: LocalDateTime,
    val category: Category,
    val author: Author,
) {
    init {
        require(title.isNotBlank()) { "Title is required" }
        require(summary.isNotBlank() && summary.length <= 500) {
            "Summary is required and must have at most 500 characters"
        }
        require(price >= 20) { "Price must be at least 20" }
        require(pages >= 100) { "Pages must be at least 100" }
        require(isbn.isNotBlank()) { "Isbn is required" }
    }
}

@Serializable
@ValidRequest
data class BookRequest(
    @NotBlank val title: String,
    @NotBlank val summary: String,
    val content: String,
    val price: Double,
    val pages: Int,
    @NotBlank val isbn: String,
    @NotBlank val publishedAtISO8601: String,
    val categoryId: Int,
    val authorId: Int,
) : Validatable {
    override suspend fun validate(): ValidationResult {
        val violations =
            buildList {
                if (summary.length > 500) {
                    add("Summary must have at most 500 characters")
                }
                if (price < 20) {
                    add("Price must be at least 20")
                }
                if (pages < 100) {
                    add("Pages must be at least 100")
                }
                if (publishedAtISO8601.run(LocalDateTime::parse).isBefore(LocalDateTime.now())) {
                    add("PublishedAt must be in the future")
                }
                if (!validateRowExist(CategoryTable, categoryId)) {
                    add("Category does not exist")
                }
                if (!validateRowExist(AuthorTable, authorId)) {
                    add("Author does not exist")
                }
            }
        return if (violations.isNotEmpty()) {
            ValidationResult.Invalid(violations)
        } else {
            ValidationResult.Valid
        }
    }
}

class BookInsert private constructor(
    val title: String,
    val summary: String,
    val content: String,
    val price: Double,
    val pages: Int,
    val isbn: String,
    val publishedAt: LocalDateTime,
    val categoryId: Int,
    val authorId: Int,
) {
    companion object {
        fun fromRequest(request: BookRequest) =
            BookInsert(
                title = request.title,
                summary = request.summary,
                content = request.content,
                price = request.price,
                pages = request.pages,
                isbn = request.isbn,
                publishedAt = LocalDateTime.parse(request.publishedAtISO8601),
                categoryId = request.categoryId,
                authorId = request.authorId,
            )
    }
}

@Serializable
data class ExposedBookList(
    val title: String,
    val id: Int,
) {
    companion object {
        fun fromRow(row: ResultRow) =
            ExposedBookList(
                title = row[BookTable.title],
                id = row[BookTable.id].value,
            )
    }
}

@Serializable
data class ExposedBook(
    val title: String,
    val summary: String,
    val content: String,
    val price: Double,
    val pages: Int,
    val isbn: String,
    val publishedAt: String,
    val author: BookAuthor,
) {
    @Serializable
    data class BookAuthor(
        val name: String,
        val description: String,
    )

    companion object {
        fun fromRow(resultRow: ResultRow): ExposedBook {
            return ExposedBook(
                title = resultRow[BookTable.title],
                summary = resultRow[BookTable.summary],
                content = resultRow[BookTable.content],
                price = resultRow[BookTable.price],
                pages = resultRow[BookTable.pages],
                isbn = resultRow[BookTable.isbn],
                publishedAt = resultRow[BookTable.publishedAt].toString(),
                author =
                    BookAuthor(
                        name = resultRow[AuthorTable.name],
                        description = resultRow[AuthorTable.description],
                    ),
            )
        }
    }
}

object BookTable : IntIdTable() {
    val title = varchar("title", length = 200)
    val summary = varchar("summary", length = 500)
    val content = text("content")
    val price = double("price")
    val pages = integer("pages")
    val isbn = varchar("isbn", length = 20)
    val publishedAt = datetime("published_at")
    val categoryId = reference("category_id", CategoryTable)
    val authorId = reference("author_id", AuthorTable)
}
