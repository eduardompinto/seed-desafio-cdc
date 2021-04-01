package eduardompinto.cdc.author

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, length = 400)
    @get:NotBlank
    val description: String,

    @Column(unique = true, nullable = false, length = 254)
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
