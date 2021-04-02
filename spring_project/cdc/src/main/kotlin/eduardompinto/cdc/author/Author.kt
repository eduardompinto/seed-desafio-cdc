package eduardompinto.cdc.author

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity(name = "authors")
@Table(
    indexes = [
        Index(name = "authors_email_idx", columnList = "email")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "authors_email_uk", columnNames = ["email"])
    ]
)
class Author(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "authors_seq"
    )
    val id: Long = 0,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, length = 400)
    @get:NotBlank
    val description: String,

    @Column(nullable = false, length = 254)
    @get:Email
    @get:NotBlank
    val email: String,

    @Column(nullable = false, length = 100)
    @get:NotBlank
    val name: String,
) {
    override fun toString(): String {
        return "Author(id=$id, createdAt=$createdAt, description='$description', name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Author
        if (email != other.email) return false
        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}
