package eduardompinto.cdc.controller

import eduardompinto.cdc.model.Author
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class AuthorRequest(
    @get:[NotBlank Email]
    val email: String,
    @get:[NotBlank]
    val name: String,
    @get:[NotBlank]
    val description: String,
) {

    fun asAuthor(): Author = Author(email = email, name = name, description = description)

    override fun toString(): String {
        return "AuthorRequest(name='$name', description='$description')"
    }
}
