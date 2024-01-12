package io.moya.shirtscanner

import io.moya.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class ShirtscannerTest : AbstractIntegrationTest() {
    @Test
    fun `application loads`() {
        When {
            get("/actuator/health")
        } Then {
            statusCode(200)
        }
    }
}
