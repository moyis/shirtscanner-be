package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ImageControllerTest : AbstractIntegrationTest() {
    @Test
    fun `returns 200`() {
        Given {
            param("path", "ok.jpg")
        } When {
            get("/v1/images/yupoo")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `returns image content`() {
        val image =
            Given {
                param("path", "ok.jpg")
            } When {
                get("/v1/images/yupoo")
            } Extract {
                body().asByteArray()
            }

        assertThat(image).isEqualTo("image".toByteArray())
    }

    @Test
    fun `returns 400 when no path param is sent`() {
        When {
            get("/v1/images/yupoo")
        } Then {
            statusCode(400)
        }
    }
}
