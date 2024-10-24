package eduardompinto.country

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.country() {
    post("/country") {
        val country = call.receive<CountryRequest>()
        val id = Repository.createCountry(Country(country.name))
        call.respond(HttpStatusCode.OK, id)
    }
}
