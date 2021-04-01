package eduardompinto.cdc.category

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 200)
    @get:NotBlank
    val name: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (name != (other as Category).name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
