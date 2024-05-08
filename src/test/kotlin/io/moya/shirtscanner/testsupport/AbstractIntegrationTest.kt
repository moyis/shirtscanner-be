package io.moya.shirtscanner.testsupport

import io.moya.shirtscanner.Shirtscanner
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [Shirtscanner::class],
)
@ContextConfiguration(initializers = [IntegrationTestConfiguration::class])
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    protected lateinit var tfs: TestFixtureService

    @BeforeEach
    fun setUpRestAssured() {
        RestAssured.port = port
    }

    @AfterEach
    fun clear() {
        tfs.clearAll()
    }
}
