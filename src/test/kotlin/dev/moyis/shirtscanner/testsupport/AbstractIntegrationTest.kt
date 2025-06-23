package dev.moyis.shirtscanner.testsupport

import dev.moyis.shirtscanner.Shirtscanner
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [Shirtscanner::class],
)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import(MongoDbTestcontainersConfiguration::class, WiremockTestcontainersConfiguration::class)
abstract class AbstractIntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    protected lateinit var tfs: TestFixtureService

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUpRestAssured() {
        RestAssured.port = port
    }

    @BeforeEach
    fun cleanUp() {
        tfs.clearAll()
    }
}
