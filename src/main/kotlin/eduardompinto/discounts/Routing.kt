package eduardompinto.discounts

import eduardompinto.discounts.Repository.createDiscountVoucher
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.discounts() {

    post("/discounts-vouchers") {
        val req = call.receive<DiscountVoucherRequest>()
        val voucherId = createDiscountVoucher(req)
        call.respond(HttpStatusCode.OK, voucherId)
    }
}