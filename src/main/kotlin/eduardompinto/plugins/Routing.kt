package eduardompinto.plugins

import eduardompinto.author.authors
import eduardompinto.book.books
import eduardompinto.category.categories
import eduardompinto.country.country
import eduardompinto.country.state.countryState
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        authors()
        categories()
        books()
        country()
        countryState()
    }
}
