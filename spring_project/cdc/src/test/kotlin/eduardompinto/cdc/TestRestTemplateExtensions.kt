package eduardompinto.cdc

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity

inline fun <reified T> TestRestTemplate.postForEntity(url: String, req: Any?): ResponseEntity<T> {
    return this.postForEntity(url, req, T::class.java)
}

inline fun <reified T> TestRestTemplate.getForEntity(url: String): ResponseEntity<T> {
    return this.getForEntity(url, T::class.java)
}
