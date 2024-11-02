package eduardompinto.country.state

import eduardompinto.country.CountryTable
import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
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

    fun countryStateExists(countryState: CountryState): Boolean {
        return CountryStateTable.join(CountryTable, JoinType.LEFT, CountryTable.id, CountryStateTable.countryId).selectAll()
            .where {
                (CountryTable.name eq countryState.country) and (CountryStateTable.name eq countryState.name)
            }.count() > 0
    }
}
