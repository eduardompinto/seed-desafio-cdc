package eduardompinto.country.state

import eduardompinto.country.CountryTable
import eduardompinto.plugins.dbQuery
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll

data class CountryState(
    val name: String,
    val country: String,
) {
    init {
        require(name.isNotBlank()) { "name cannot be blank" }
        require(country.isNotBlank()) { "country cannot be blank" }
    }
}

@Serializable
data class CountryStateRequest(
    val name: String,
    val country: String,
) {
    suspend fun validate(): ValidationResult {
        val reasons =
            buildList {
                if (name.isBlank()) {
                    add("name cannot be blank")
                }
                if (country.isBlank()) {
                    add("country cannot be blank")
                }
                dbQuery {
                    addLogger(StdOutSqlLogger)
                    CountryStateTable.selectAll().where { CountryStateTable.name eq name }.union(
                        CountryTable.selectAll().where { CountryTable.name eq country },
                    ).run {
                        val countryId = this.firstOrNull { it.hasValue(CountryTable.name) }?.get(CountryTable.id)
                        if (countryId == null) {
                            add("country does not exist")
                        } else if (
                            this.any { it.hasValue(CountryStateTable.name) && it[CountryStateTable.countryId] == countryId }
                        ) {
                            add("state already exists")
                        }
                    }
                }
            }
        return when {
            reasons.isNotEmpty() -> ValidationResult.Invalid(reasons)
            else -> ValidationResult.Valid
        }
    }
}

object CountryStateTable : IntIdTable() {
    val name = varchar("name", length = 200)
    val countryId = reference("country_id", CountryTable)

    init {
        uniqueIndex(name, countryId)
    }
}
