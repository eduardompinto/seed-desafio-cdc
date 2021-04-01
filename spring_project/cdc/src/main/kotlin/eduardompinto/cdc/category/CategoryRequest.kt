package eduardompinto.cdc.category

import javax.validation.constraints.NotBlank

data class CategoryRequest(
    @get:[NotBlank]
    val name: String,
) {
    fun asCategory(): Category = Category(name = name)
}
