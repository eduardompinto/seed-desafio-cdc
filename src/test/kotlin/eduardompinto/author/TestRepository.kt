package eduardompinto.author

import eduardompinto.infra.TestDatabase
import eduardompinto.infra.TestDatabase.database
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDateTime

@DisplayName("Author Repository")
class TestRepository {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            TestDatabase.createTables(AuthorTable)
        }

        @AfterAll
        @JvmStatic
        fun destroy() {
            TestDatabase.stop()
        }

        @JvmStatic
        fun notBlankColumns() =
            listOf(
                AuthorTable.email,
                AuthorTable.createdAt,
                AuthorTable.description,
                AuthorTable.name,
            )
    }

    private val repository = Repository

    @Test
    @DisplayName("email is unique")
    fun testCreateAuthorUniqueEmail() {
        val author =
            Author(
                name = "Eduardo Pinto",
                description = "Software Engineer",
                email = "edu@gmail.com",
            )
        val author2 =
            author.copy(
                name = "Eduardo Pinto 2",
                description = "Software Engineer 2",
            )
        runBlocking {
            repository.create(author)
            assertThrows<SQLException> {
                repository.create(author2)
            }.also {
                assertInstanceOf<SQLIntegrityConstraintViolationException>(it.cause)
            }
        }
    }

    @Nested
    @DisplayName("Table Constraints")
    inner class TestTableConstraints {
        @Test
        @DisplayName("description cannot be longer than 400 characters")
        fun testCreateAuthorDescriptionLength() {
            assertThrows<IllegalArgumentException> {
                transaction(database) {
                    AuthorTable.insert { statement ->
                        statement[name] = "Edu"
                        statement[description] = "n".repeat(401)
                        statement[email] = "email@gmail.com"
                        statement[createdAt] = LocalDateTime.now()
                    }[AuthorTable.id]
                }
            }
        }

        @Test
        @DisplayName("name cannot be longer than 50 characters")
        fun testCreateAuthorNameEmpty() {
            assertThrows<IllegalArgumentException> {
                transaction(database) {
                    AuthorTable.insert { statement ->
                        statement[name] = "n".repeat(51)
                        statement[description] = "n"
                        statement[email] = "email@gmail.com"
                        statement[createdAt] = LocalDateTime.now()
                    }[AuthorTable.id]
                }
            }
        }

        @ParameterizedTest
        @MethodSource("eduardompinto.author.TestRepository#notBlankColumns")
        fun testNotNullConstraints(column: Column<*>) {
            assertThrows<SQLException> {
                transaction(database) {
                    AuthorTable.insert { statement ->
                        if (column != description) statement[description] = "n"
                        if (column != name) statement[name] = "name"
                        if (column != email) statement[email] = "email"
                        if (column != createdAt) statement[createdAt] = LocalDateTime.now()
                    }
                }
            }.also {
                assertInstanceOf<SQLIntegrityConstraintViolationException>(it.cause)
            }
        }
    }
}
