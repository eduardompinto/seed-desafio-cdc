package eduardompinto.category

import eduardompinto.plugins.NotBlank
import eduardompinto.plugins.UniqueStringField
import eduardompinto.plugins.ValidRequest
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

data class Category(val name: String) {
    init {
        require(name.isNotBlank()) { "name cannot be blank" }
    }
}

@Serializable
@ValidRequest
data class CategoryRequest(
    @NotBlank
    @UniqueStringField(CategoryTable::class, "name")
    val name: String,
) {
    fun toCategory() = Category(name)
}

@Serializable
data class ExposedCategory(
    val name: String,
) {
    companion object {
        fun fromCategory(category: Category) = ExposedCategory(category.name)
    }
}

object CategoryTable : IntIdTable() {
    val name = varchar("name", length = 200).uniqueIndex()
}
