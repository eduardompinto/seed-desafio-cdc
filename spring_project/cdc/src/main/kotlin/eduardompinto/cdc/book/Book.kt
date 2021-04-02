package eduardompinto.cdc.book

import eduardompinto.cdc.author.Author
import eduardompinto.cdc.category.Category
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Future
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity(name = "books")
@Table(
    indexes = [
        Index(name = "books_title_idx", columnList = "title"),
        Index(name = "books_isbn_idx", columnList = "isbn")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "books_title_uk", columnNames = ["title"]),
        UniqueConstraint(name = "books_isbn_uk", columnNames = ["isbn"]),
    ],
)
class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "books_seq")
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(length = 500, nullable = false)
    @get:Size(max = 500)
    val abstract: String,

    @Column(columnDefinition = "TEXT", nullable = true)
    val summary: String?,

    @Column(nullable = false)
    @get:Min(20)
    val price: BigDecimal,

    @Column(nullable = false)
    @get:Min(100)
    val numberOfPages: Int,

    @Column(nullable = false)
    @get:Size(min = 10, max = 13)
    val ISBN: String,

    @Column(nullable = true)
    @get:Future
    val publishDate: Instant?,

    @ManyToOne(
        targetEntity = Author::class,
        fetch = FetchType.EAGER
    )
    @JoinColumn(
        name = "author_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "book_author_fk")
    )
    val author: Author,

    @ManyToOne(
        targetEntity = Category::class,
        fetch = FetchType.EAGER,
    )
    @JoinColumn(
        name = "category_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "book_category_fk")
    )
    val category: Category,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (ISBN != (other as Book).ISBN) return false
        return true
    }

    override fun hashCode(): Int {
        return ISBN.hashCode()
    }

    override fun toString(): String {
        return "Book(title='$title', ISBN='$ISBN')"
    }
}
