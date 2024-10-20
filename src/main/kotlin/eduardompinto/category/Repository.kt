package eduardompinto.category

import eduardompinto.plugins.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object Repository {
    suspend fun createCategory(category: Category): Int =
        dbQuery {
            CategoryTable.insert { statement ->
                statement[name] = category.name
            }[CategoryTable.id].value
        }

    suspend fun readCategory(id: Int): Category? {
        return dbQuery {
            CategoryTable.selectAll().where { CategoryTable.id eq id }
                .map { row ->
                    Category(name = row[CategoryTable.name])
                }
                .singleOrNull()
        }
    }
}
