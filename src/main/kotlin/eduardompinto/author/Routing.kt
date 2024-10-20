package eduardompinto.author

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.text.toInt

fun Routing.authors() {
    post("/authors") {
        val author = call.receive<AuthorRequest>()
        val id = Repository.create(author.toAuthor())
        call.respond(HttpStatusCode.OK, id)
    }

    get("/authors/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val result = Repository.read(id)
        when (result) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.OK, AuthorResponse.fromAuthor(result))
        }
    }
}
