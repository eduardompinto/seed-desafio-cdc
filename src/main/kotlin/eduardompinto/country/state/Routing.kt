package eduardompinto.country.state

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.countryState() {
    post("/country-state") {
        val countryStateRequest = call.receive<CountryStateRequest>()
        val id = Repository.createCountryState(CountryState(countryStateRequest.name, countryStateRequest.country))
        call.respond(HttpStatusCode.OK, id)
    }
}
