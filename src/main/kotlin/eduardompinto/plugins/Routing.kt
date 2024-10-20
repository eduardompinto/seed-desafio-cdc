package eduardompinto.plugins

import eduardompinto.author.authors
import eduardompinto.category.categories
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        authors()
        categories()
    }
}
