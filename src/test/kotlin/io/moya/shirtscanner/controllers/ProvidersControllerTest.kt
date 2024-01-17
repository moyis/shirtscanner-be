package io.moya.shirtscanner.controllers

import io.moya.shirtscanner.configuration.ProviderData
import io.moya.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProvidersControllerTest : AbstractIntegrationTest() {
    @Test
    fun `providers endpoint returns 200`() {
        When {
            get("/v1/providers")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `providers endpoint returns all configured providers`() {
        val providers =
            When {
                get("/v1/providers")
            } Extract {
                body().jsonPath().getList("", ProviderData::class.java)
            }
        assertThat(providers).extracting<String> { it.name }.containsExactlyInAnyOrder("ListR1 Test", "Yupoo Test")
    }
}
