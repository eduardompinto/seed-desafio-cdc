package eduardompinto.cdc.category

import eduardompinto.cdc.getForEntity
import eduardompinto.cdc.postForEntity
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
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Given a route /categories/ that is used to manage categories")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // To run the afterEach
class CategoryControllerTest(
    @LocalServerPort port: Int,
    @Autowired private val restTemplate: TestRestTemplate,
    @Autowired private val repository: CategoryRepository,
) {

    private val url = "http://localhost:$port/categories/"

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    @DisplayName("when the category name is new, POST should save a new category and answer 200(OK)")
    fun saveNewCategory() {
        val req = CategoryRequest(name = "Java")
        with(restTemplate.postForEntity<Category>(url = url, req = req)) {
            assertThat(statusCode).isEqualTo(OK)
            assertThat(body).isNotNull
            assertThat(body!!.id).isGreaterThan(0)
            assertThat(body).isEqualTo(req.asCategory())
        }
    }

    @Test
    @DisplayName("when the category name already exists, POST should return 400(BAD_REQUEST)")
    fun failToSavedDuplicatedCategory() {
        val req = CategoryRequest(name = "Kotlin")
        repository.save(req.asCategory())
        with(restTemplate.postForEntity<String>(url = url, req = req)) {
            assertThat(statusCode).isEqualTo(BAD_REQUEST)
        }
    }

    @Test
    @DisplayName("when the category name is empty or null, POST should return 400(BAD REQUEST)")
    fun badRequestOnName() {
        listOf("", null).forEach { name ->
            val req: Map<String, String?> = buildPayload(name = name)
            with(restTemplate.postForEntity<String>(url = url, req = req)) {
                assertThat(statusCode).isEqualTo(BAD_REQUEST)
            }
        }
    }

    @Test
    @DisplayName("when the category exists, GET should return it and 200(OK)")
    fun getExistingCategory() {
        val req = CategoryRequest(name = "Kotlin")
        val existingCategory = repository.save(req.asCategory())
        with(restTemplate.getForEntity<Category>("$url${existingCategory.id}")) {
            assertThat(statusCode).isEqualTo(OK)
            assertThat(body).isEqualTo(req.asCategory())
            assertThat(body.id).isEqualTo(existingCategory.id)
        }
    }

    @Test
    @DisplayName("when the category doesn't exists, GET should return 404(NOT FOUND)")
    fun getUnexistingCategory() {
        with(restTemplate.getForEntity<Unit>("${url}999")) {
            assertThat(statusCode).isEqualTo(NOT_FOUND)
        }
    }

    private fun buildPayload(name: String?): Map<String, String?> = mapOf("name" to name)
}
