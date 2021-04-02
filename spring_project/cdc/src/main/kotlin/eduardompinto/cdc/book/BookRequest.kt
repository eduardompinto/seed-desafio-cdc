package eduardompinto.cdc.book

import eduardompinto.cdc.author.Author
import eduardompinto.cdc.category.Category
import java.math.BigDecimal
import java.time.Instant
import javax.validation.constraints.Future
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class BookRequest(
    @get:NotBlank
    val title: String,
    @get:[Size(max = 500) NotBlank]
    val abstract: String,
    @get:NotBlank
    val summary: String?,
    @get:Min(20)
    val price: BigDecimal,
    @get:Min(100)
    val numberOfPages: Int,
    @get:Size(max = 13, min = 10)
    val ISBN: String,
    @get:Future
    val publishDate: Instant?,
    val author: Long,
    val category: Long,
) {

    fun asBook(author: Author, category: Category) = Book(
        title = title,
        abstract = abstract,
        summary = summary,
        price = price,
        numberOfPages = numberOfPages,
        ISBN = ISBN,
        publishDate = publishDate,
        category = category,
        author = author
    )
}
