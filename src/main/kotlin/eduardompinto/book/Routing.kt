package eduardompinto.book

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.books() {
    post("/books") {
        val bookRequest = call.receive<BookRequest>()
        val id = Repository.create(BookInsert.fromRequest(bookRequest))
        call.respond(HttpStatusCode.OK, id)
    }
}
