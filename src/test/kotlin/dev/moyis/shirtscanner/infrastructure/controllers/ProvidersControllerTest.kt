package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.infrastructure.controllers.model.ProviderResponse
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

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
    fun `providers endpoint returns status for providers`() {
        tfs.persistProviderData(
            ProviderData(
                URI("https://listr1.com"),
                ProviderName("ListR1 Test"),
                ProviderStatus.UP,
            ),
            ProviderData(
                URI("https://yupoo.com"),
                ProviderName("Yupoo Test"),
                ProviderStatus.DOWN,
            )
        )
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

    @Test
    fun `checks status when post providers`() {
        When {
            post("/v1/providers")
        }

        val providers =
            When {
                get("/v1/providers")
            } Extract {
                body().jsonPath().getList("", ProviderResponse::class.java)
            }

        assertThat(providers)
            .extracting<ProviderStatus> { it.status }
            .containsOnly(ProviderStatus.DOWN)
    }
}
