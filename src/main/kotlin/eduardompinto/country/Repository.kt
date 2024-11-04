package eduardompinto.country

import eduardompinto.country.state.CountryStateTable
import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object Repository {
    suspend fun createCountry(country: Country) =
        dbQuery {
            CountryTable.insert {
                it[name] = country.name
            }[CountryTable.id].value
        }

    suspend fun countryHasStates(countryId: Int): Boolean =
        dbQuery {
            addLogger(StdOutSqlLogger)
            CountryTable.join(CountryStateTable, JoinType.LEFT, CountryTable.id, CountryStateTable.countryId)
                .selectAll()
                .where {
                    CountryTable.id eq countryId
                }.count() > 0
        }

    suspend fun findCountryId(country: Country) =
        dbQuery {
            CountryTable.select(CountryTable.id).where {
                CountryTable.name eq country.name
            }.firstOrNull()?.get(CountryTable.id)?.value
        }
}
