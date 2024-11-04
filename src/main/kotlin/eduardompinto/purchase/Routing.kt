package eduardompinto.purchase

import eduardompinto.purchase.Repository.createPurchase
import eduardompinto.purchase.Repository.findPurchaseById
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Routing.purchases() {
    post("/purchases") {
        val req = call.receive<PurchaseRequest>()
        val purchaseId = createPurchase(req)
        // TODO: user ktor resources
        call.respond(HttpStatusCode.Created, mapOf("purchase" to "http://localhost:8080/purchases/$purchaseId"))
    }

    get("/purchases/{id}") {
        val id = call.parameters["id"]?.toInt()
        requireNotNull(id)
        val purchase = findPurchaseById(id) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(HttpStatusCode.OK, PurchaseResponse(purchase))
    }
}
