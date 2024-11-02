package eduardompinto.country

import eduardompinto.country.state.CountryState
import eduardompinto.plugins.UniqueStringField
import eduardompinto.plugins.ValidRequest
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

data class Country(
    val name: String,
    val states: Lazy<List<CountryState>> = lazy { emptyList() },
) {
    init {
        require(name.isNotBlank()) { "name cannot be blank" }
    }
}

@Serializable
@ValidRequest
data class CountryRequest(
    @UniqueStringField(CountryTable::class, "name")
    val name: String,
)

object CountryTable : IntIdTable() {
    val name = varchar("name", length = 200).uniqueIndex()
}
