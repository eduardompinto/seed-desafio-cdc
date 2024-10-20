package eduardompinto

import eduardompinto.plugins.configureDatabases
import eduardompinto.plugins.configureSerialization
import eduardompinto.plugins.configureStatusPage
import eduardompinto.plugins.configureValidation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            application {
                main()
            }
            client.post("/authors") {
                setBody(
                    buildJsonObject {
                        put("name", JsonPrimitive("Eduardo"))
                        put("email", JsonPrimitive("invalid"))
                        put("description", JsonPrimitive(""))
                    }.toString(),
                )
                contentType(ContentType.Application.Json)
            }.apply {
                assertEquals(status.value, 200)
            }
        }

    private fun Application.main() {
        configureSerialization()
        configureValidation()
        configureStatusPage()
        configureDatabases()
    }
}
