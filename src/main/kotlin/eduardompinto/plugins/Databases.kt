package eduardompinto.plugins

import eduardompinto.author.AuthorTable
import eduardompinto.book.BookTable
import eduardompinto.category.CategoryTable
import eduardompinto.country.CountryTable
import eduardompinto.country.state.CountryStateTable
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

val database by lazy {
    Database.connect(
        url = "jdbc:pgsql://localhost:5432/bookstore",
        user = "postgres",
        password = "postgres",
        driver = "com.impossibl.postgres.jdbc.PGDriver",
    )
}

fun Application.configureDatabases() {
    database
    transaction {
        SchemaUtils.create(*tables.toTypedArray())
    }
}

suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

val tables =
    listOf<Table>(
        AuthorTable,
        CategoryTable,
        BookTable,
        CountryTable,
        CountryStateTable,
    )
