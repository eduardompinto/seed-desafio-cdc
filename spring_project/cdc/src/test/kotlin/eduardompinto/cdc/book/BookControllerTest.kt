// package eduardompinto.cdc.book
//
// import eduardompinto.cdc.author.Author
// import eduardompinto.cdc.author.AuthorRepository
// import eduardompinto.cdc.category.Category
// import eduardompinto.cdc.category.CategoryRepository
// import org.junit.jupiter.api.AfterEach
// import org.junit.jupiter.api.BeforeAll
// import org.junit.jupiter.api.DisplayName
// import org.junit.jupiter.api.Test
// import org.junit.jupiter.api.TestInstance
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
// import org.springframework.boot.test.web.client.TestRestTemplate
// import org.springframework.boot.web.server.LocalServerPort
// import org.springframework.transaction.annotation.Transactional
// import java.time.Instant
//
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @DisplayName("Given a route /books/ that is used to manage categories")
// @TestInstance(TestInstance.Lifecycle.PER_CLASS) // To run the afterEach
// @Transactional
// class BookControllerTest(
//    @LocalServerPort port: Int,
//    @Autowired private val restTemplate: TestRestTemplate,
//    @Autowired private val booksRepository: BookRepository,
//    @Autowired private val authorRepository: AuthorRepository,
//    @Autowired private val categoryRepository: CategoryRepository,
// ) {
//
//    private val url = "http://localhost:$port/books/"
//
//    lateinit var author: Author
//    lateinit var category: Category
//
//    @AfterEach
//    fun cleanUp() {
//        booksRepository.deleteAll()
//    }
//
//    @BeforeAll
//    fun beforeAll() {
//        author = Author(
//            description = "test author",
//            name = "test",
//            email = "test@example.com"
//        ).run(authorRepository::save)
//        category = Category(name = "java").run(categoryRepository::save)
//    }
//
//    @Test
//    fun saveNewBook() {
//    }
// }
