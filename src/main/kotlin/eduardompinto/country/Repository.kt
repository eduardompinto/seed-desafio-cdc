package eduardompinto.country

import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.insert

object Repository {
    suspend fun createCountry(country: Country) =
        dbQuery {
            CountryTable.insert {
                it[name] = country.name
            }[CountryTable.id].value
        }
}
