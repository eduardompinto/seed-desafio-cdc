package eduardompinto.book

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Routing.books() {
    post("/books") {
        val bookRequest = call.receive<BookRequest>()
        val id = Repository.create(BookInsert.fromRequest(bookRequest))
        call.respond(HttpStatusCode.OK, id)
    }

    get("/books") {
        val books = Repository.findAll(ExposedBookList::fromRow)
        call.respond(HttpStatusCode.OK, books)
    }

    get("/books/{id}") {
        when (val id = call.parameters["id"]?.toIntOrNull()) {
            null -> call.respond(HttpStatusCode.BadRequest)
            else ->
                when (val book = Repository.findBookWithAuthor(id, ExposedBook::fromRow)) {
                    null -> call.respond(HttpStatusCode.NotFound)
                    else -> call.respond(HttpStatusCode.OK, book)
                }
        }
    }
}
