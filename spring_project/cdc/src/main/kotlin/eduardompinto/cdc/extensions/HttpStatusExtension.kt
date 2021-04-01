package eduardompinto.cdc.extensions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> HttpStatus.build(t: T? = null): ResponseEntity<T> =
    ResponseEntity.status(this).body(t)
