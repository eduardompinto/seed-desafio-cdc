package eduardompinto.purchase

import eduardompinto.infra.TestDatabase
import eduardompinto.plugins.configureSerialization
import eduardompinto.plugins.configureStatusPage
import eduardompinto.plugins.configureValidation
import eduardompinto.plugins.tables
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertEquals

class TestPurchaseAPI {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            TestDatabase.createTables(*tables.toTypedArray())
        }

        @AfterAll
        @JvmStatic
        fun destroy() {
            TestDatabase.stop()
        }
    }

    @Test
    fun testMakePurchase() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(200, response.status.value)
                assertEquals("application/json", response.contentType()?.toString())
            }
        }
    }

    @Test
    fun testMissingEmail() {
        val purchaseRequest =
            PurchaseRequest(
                email = "",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing email", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testInvalidEmail() {
        val purchaseRequest =
            PurchaseRequest(
                email = "invalid-email",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Invalid email format", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testMissingName() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing name", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testMissingLastName() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing last name", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testInvalidDocument() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "Doe",
                document = "1234567890",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Invalid document", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testMissingAddress() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing address", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testMissingComplement() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "",
                city = "Sao Paulo",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing complement", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testMissingCity() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "",
                country = "Brazil",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing city", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    @Test
    fun testMissingCountry() {
        val purchaseRequest =
            PurchaseRequest(
                email = "john.doe@example.com",
                name = "John",
                lastName = "Doe",
                document = "123456789",
                address = "123 Main St",
                complement = "Apt 4B",
                city = "Sao Paulo",
                country = "",
                countryState = "SP",
                phone = "32222222",
                postCode = "05105000",
            )

        testApplication {
            application {
                testPurchase()
            }
            client.post("/purchases") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(purchaseRequest))
            }.let { response ->
                assertEquals(422, response.status.value)
                val errorResponse = response.body<JsonObject>()
                assertEquals("Missing country", errorResponse["message"]?.jsonPrimitive?.content)
            }
        }
    }

    private fun Application.testPurchase() {
        configureSerialization()
        configureValidation()
        configureStatusPage()
        routing {
            purchases()
        }
    }
}
