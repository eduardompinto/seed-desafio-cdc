package eduardompinto.cdc.extensions

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import java.util.Optional

fun <T> Optional<T>.okOrNotFound(): ResponseEntity<T> = when {
    isPresent -> OK.build(get())
    else -> NOT_FOUND.build()
}
