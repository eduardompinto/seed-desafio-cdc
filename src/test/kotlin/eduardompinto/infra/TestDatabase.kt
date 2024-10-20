package eduardompinto.infra

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

object TestDatabase {
    private val container: PostgreSQLContainer<Nothing> by lazy {
        PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("test-db")
            withUsername("test-user")
            withPassword("test-password")
        }
    }

    val database: Database by lazy {
        if (!container.isRunning || !container.isHealthy) {
            container.start()
        }

        Database.connect(
            url = "jdbc:pgsql://localhost:${container.firstMappedPort}/test-db",
            user = "test-user",
            password = "test-password",
            driver = "com.impossibl.postgres.jdbc.PGDriver",
        )
    }

    fun stop() {
        container.stop()
    }

    fun createTables(vararg tables: Table) {
        transaction(database) {
            SchemaUtils.create(*tables)
        }
    }
}
