package eduardompinto.purchase

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.purchases() {
    post("/purchases") {
        val req = call.receive<PurchaseRequest>()
        call.respond(HttpStatusCode.OK)
    }
}
