package eduardompinto

import eduardompinto.plugins.configureDatabases
import eduardompinto.plugins.configureMonitoring
import eduardompinto.plugins.configureRouting
import eduardompinto.plugins.configureSerialization
import eduardompinto.plugins.configureStatusPage
import eduardompinto.plugins.configureValidation
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureRouting()
    configureStatusPage()
    configureValidation()
}
