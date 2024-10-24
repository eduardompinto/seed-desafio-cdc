package eduardompinto.country.state

import eduardompinto.country.CountryTable
import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object Repository {
    suspend fun createCountryState(countryState: CountryState) =
        dbQuery {
            val countryIdx =
                CountryTable.selectAll().where { CountryTable.name eq countryState.country }.first()[CountryTable.id]
            CountryStateTable.insert {
                it[name] = countryState.name
                it[countryId] = countryIdx.value
            }[CountryStateTable.id].value
        }
}
