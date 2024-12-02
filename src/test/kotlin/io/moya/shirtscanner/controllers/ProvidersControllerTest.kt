package io.moya.shirtscanner.controllers

import io.moya.shirtscanner.services.providers.model.ProviderResponse
import io.moya.shirtscanner.services.providers.model.ProviderStatus
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
                body().jsonPath().getList("", ProviderResponse::class.java)
            }
        assertThat(providers).extracting<String> { it.name }.containsExactlyInAnyOrder("ListR1 Test", "Yupoo Test")
    }

    @Test
    fun `providers endpoint returns status for providesrs`() {
        tfs.persistStatus("ListR1 Test", ProviderStatus.UP)
        tfs.persistStatus("Yupoo Test", ProviderStatus.DOWN)
        val providers =
            When {
                get("/v1/providers")
            } Extract {
                body().jsonPath().getList("", ProviderResponse::class.java)
            }
        assertThat(providers)
            .extracting<ProviderStatus> { it.status }
            .containsExactlyInAnyOrder(ProviderStatus.UP, ProviderStatus.DOWN)
    }

    @Test
    fun `providers endpoint returns UNKNOWN when no status is persisted`() {
        val providers =
            When {
                get("/v1/providers")
            } Extract {
                body().jsonPath().getList("", ProviderResponse::class.java)
            }
        assertThat(providers)
            .extracting<ProviderStatus> { it.status }
            .containsExactlyInAnyOrder(ProviderStatus.UNKNOWN, ProviderStatus.UNKNOWN)
    }
}
