package eduardompinto.author

import eduardompinto.plugins.UniqueStringField
import eduardompinto.plugins.isValidEmail
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.Instant

/**
 * Represents the business model for an Author.
 * This class should never be exposed out of the Application.
 */
data class Author(
    val name: String,
    val email: String,
    val description: String,
    val createdAt: Instant = Instant.now(),
) {
    init {
        require(name.isNotBlank()) { "name cannot be blank" }
        require(description.isNotBlank()) { "description cannot be blank" }
        require(description.length <= 400) { "description cannot be greater than 400 characters" }
        require(email.isNotBlank()) { "email cannot be blank" }
        require(isValidEmail(email)) { "email has to be valid" }
    }
}

/**
 * Represents the request model for an Author.
 * This class should be used to receive data from the client.
 */
@Serializable
data class AuthorRequest(
    @UniqueStringField(AuthorTable::class, "name") val name: String,
    val email: String,
    val description: String,
) {
    fun validate(): ValidationResult {
        val violations =
            buildList {
                if (name.isBlank()) {
                    add("name cannot be blank")
                }
                if (description.isBlank()) {
                    add("description cannot be blank")
                }
                if (description.length > 400) {
                    add("description cannot be greater than 400 characters")
                }
                if (email.isBlank()) {
                    add("email cannot be blank")
                }
                if (!isValidEmail(email)) {
                    add("email has to be valid")
                }
            }
        return if (violations.isNotEmpty()) {
            ValidationResult.Invalid(violations)
        } else {
            ValidationResult.Valid
        }
    }

    fun toAuthor() = Author(name, email, description)
}

/**
 * Represents the response model for an Author.
 * This class should be used to send data to the client.
 */
@Serializable
data class ExposedAuthor(
    val name: String,
    val email: String,
    val description: String,
) {
    companion object {
        fun fromAuthor(author: Author) = ExposedAuthor(author.name, author.email, author.description)
    }
}

/**
 * Represents the database schema for an Author.
 * This class should be used to create the database table.
 */
object AuthorTable : IntIdTable() {
    val name = varchar("name", length = 50)
    val email = varchar("email", length = 255).uniqueIndex()
    val description = varchar("description", length = 400)
    val createdAt = datetime("created_at")
}
