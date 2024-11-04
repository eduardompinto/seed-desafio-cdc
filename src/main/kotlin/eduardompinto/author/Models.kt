package eduardompinto.author

import eduardompinto.commons.Email
import eduardompinto.commons.Email.Companion.asEmail
import eduardompinto.plugins.NotBlank
import eduardompinto.plugins.UniqueStringField
import eduardompinto.plugins.ValidRequest
import eduardompinto.plugins.Validatable
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
    val email: Email,
    val description: String,
    val createdAt: Instant = Instant.now(),
) {
    init {
        require(name.isNotBlank()) { "name cannot be blank" }
        require(description.isNotBlank()) { "description cannot be blank" }
        require(description.length <= 400) { "description cannot be greater than 400 characters" }
    }
}

/**
 * Represents the request model for an Author.
 * This class should be used to receive data from the client.
 */
@Serializable
@ValidRequest
data class AuthorRequest(
    @NotBlank @UniqueStringField(AuthorTable::class, "name") val name: String,
    @NotBlank @UniqueStringField(AuthorTable::class, "email") val email: String,
    @NotBlank val description: String,
) : Validatable {
    override suspend fun validate(): ValidationResult {
        val violations =
            buildList {
                if (description.length > 400) {
                    add("description cannot be greater than 400 characters")
                }
                if (!Email.isValid(email)) {
                    add("email has to be valid")
                }
            }
        return if (violations.isNotEmpty()) {
            ValidationResult.Invalid(violations)
        } else {
            ValidationResult.Valid
        }
    }

    fun toAuthor() = Author(name, email.asEmail(), description)
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
        fun fromAuthor(author: Author) = ExposedAuthor(author.name, author.email.value, author.description)
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
