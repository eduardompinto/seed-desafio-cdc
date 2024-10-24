package eduardompinto.category

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Routing.categories() {
    post("/categories") {
        val category = call.receive<CategoryRequest>()
        val id: Int = Repository.createCategory(category.toCategory())
        call.respond(HttpStatusCode.OK, id)
    }

    get("/categories/{id}") {
        val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
        val result = Repository.readCategory(id)
        when (result) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.OK, ExposedCategory.fromCategory(result))
        }
    }
}
