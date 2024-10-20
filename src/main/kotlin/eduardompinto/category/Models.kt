package eduardompinto.category

import eduardompinto.plugins.UniqueStringField
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

data class Category(val name: String) {
    init {
        require(name.isNotBlank()) { "name cannot be blank" }
    }
}

@Serializable
data class CategoryRequest(
    @UniqueStringField(CategoryTable::class, "name")
    val name: String,
) {
    fun validate(): ValidationResult.Valid {
        val reasons =
            buildList {
                if (name.isBlank()) {
                    add("name cannot be blank")
                }
            }
        if (reasons.isNotEmpty()) {
            ValidationResult.Invalid(reasons)
        }
        return ValidationResult.Valid
    }

    fun toCategory() = Category(name)
}

@Serializable
data class CategoryResponse(
    val name: String,
) {
    companion object {
        fun fromCategory(category: Category) = CategoryResponse(category.name)
    }
}

object CategoryTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 200).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}
