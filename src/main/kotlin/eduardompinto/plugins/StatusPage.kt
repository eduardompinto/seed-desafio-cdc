package eduardompinto.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.sql.SQLIntegrityConstraintViolationException

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<BadRequestException> { call: ApplicationCall, cause: BadRequestException ->
            when (cause.cause) {
                is IllegalArgumentException, is JsonConvertException -> {
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        cause.cause!!.message ?: "Failed to validate the request",
                    )
                }

                else -> call.respond(HttpStatusCode.BadRequest)
            }
        }

        exception<ExposedSQLException> { call, cause ->
            when (cause.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    call.respond(HttpStatusCode.Conflict, cause.message ?: "Failed to validate the request")
                }
            }
        }

        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.UnprocessableEntity, cause.reasons.joinToString())
        }
    }
}
