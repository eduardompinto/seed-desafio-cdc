package eduardompinto.cdc.controller

import eduardompinto.cdc.model.Author
import eduardompinto.cdc.repository.AuthorRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Given a route /authors/ that is used to manage authors")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // To run the afterEach
class AuthorControllerTest(
    @LocalServerPort port: Int,
    @Autowired private val restTemplate: TestRestTemplate,
    @Autowired private val authorRepository: AuthorRepository,
) {

    private val url = "http://localhost:$port/authors/"

    @AfterEach
    fun cleanUp() {
        authorRepository.deleteAll()
    }

    @Test
    @DisplayName("when the author email is new, POST should save a new author and answer 200(OK)")
    fun saveNewAuthor() {
        val req = AuthorRequest(
            email = "new_author@example.com",
            name = "New Author",
            description = "test for a new author"
        )
        with(restTemplate.postForEntity<Author>(url = url, req = req)) {
            assertThat(statusCode).isEqualTo(OK)
            assertThat(body).isNotNull
            assertThat(body!!.id).isGreaterThan(0)
            assertThat(body).isEqualTo(req.asAuthor())
        }
    }

    @Test
    @DisplayName("when the author email already exists, POST should return 409(CONFLICT)")
    fun failToSavedDuplicatedAuthor() {
        val req = AuthorRequest(
            email = "new_author@example.com",
            name = "New Author",
            description = "test for a new author"
        )
        // First request to created the author
        with(restTemplate.postForEntity<Author>(url = url, req = req)) {
            assertThat(statusCode).isEqualTo(OK)
        }
        with(restTemplate.postForEntity<Author>(url = url, req = req)) {
            assertThat(statusCode).isEqualTo(CONFLICT)
        }
    }

    @Test
    @DisplayName("when the author email is invalid or null, POST should return 400(BAD REQUEST)")
    fun badRequestOnEmail() {
        listOf("", "invalid_email", null).forEach { email ->
            val req: Map<String, String?> = buildPayload(
                email = email,
                name = "New Author",
                description = "test for a new author"
            )
            with(restTemplate.postForEntity<String>(url = url, req = req)) {
                assertThat(statusCode).isEqualTo(BAD_REQUEST)
            }
        }
    }

    @Test
    @DisplayName("when the author name is empty or null, POST should return 400(BAD REQUEST)")
    fun badRequestOnName() {
        listOf("", null).forEach { name ->
            val req: Map<String, String?> = buildPayload(
                name = name,
                email = "valid@example.com",
                description = "Valid"
            )
            with(restTemplate.postForEntity<String>(url = url, req = req)) {
                assertThat(statusCode).isEqualTo(BAD_REQUEST)
            }
        }
    }

    @Test
    @DisplayName("when the author description is empty or null, POST should return 400(BAD REQUEST)")
    fun badRequestOnDescription() {
        listOf("", null).forEach { description ->
            val req: Map<String, String?> = buildPayload(
                name = "Valid",
                email = "valid@example.com",
                description = description
            )
            with(restTemplate.postForEntity<String>(url = url, req = req)) {
                assertThat(statusCode).isEqualTo(BAD_REQUEST)
            }
        }
    }

    @Test
    @DisplayName("when the author exists, GET should return it and 200(OK)")
    fun getExistingAuthor() {
        val existingAuthor: Author = authorRepository.save(
            Author(name = "Test", description = "Test author", email = "test@example.com")
        )
        with(restTemplate.getForEntity<Author>("$url${existingAuthor.id}")) {
            assertThat(statusCode).isEqualTo(OK)
            assertThat(body).isEqualTo(existingAuthor)
        }
    }

    @Test
    @DisplayName("when the author doesn't exists, GET should return 404(NOT FOUND)")
    fun getUnexistingAuthor() {
        with(restTemplate.getForEntity<Unit>("${url}999")) {
            assertThat(statusCode).isEqualTo(NOT_FOUND)
        }
    }

    private fun buildPayload(email: String?, name: String?, description: String?): Map<String, String?> =
        mapOf(
            "email" to email,
            "name" to name,
            "description" to description
        )
}
