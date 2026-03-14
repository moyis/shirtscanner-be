package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.http.HttpStatus
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.everyItem
import org.junit.jupiter.api.Test
import java.net.URI

class ProvidersControllerTest : AbstractIntegrationTest() {
    @Test
    fun `providers endpoint returns 200`() {
        When {
            get("/v1/providers")
        } Then {
            statusCode(HttpStatus.OK.value())
        }
    }

    @Test
    fun `providers endpoint returns all configured providers`() {
        When {
            get("/v1/providers")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("name", containsInAnyOrder("ListR1 Test", "Yupoo Test"))
        }
    }

    @Test
    fun `providers endpoint returns status for providers`() {
        tfs.persistProviderData(
            aValidProvider().copy(name = ProviderName("ListR1 Test"), status = ProviderStatus.UP),
            aValidProvider().copy(name = ProviderName("Yupoo Test"), status = ProviderStatus.DOWN),
        )
        When {
            get("/v1/providers")
        } Then {
            body("status", containsInAnyOrder("UP", "DOWN"))
        }
    }

    @Test
    fun `returns status UNKNOWN when no status is persisted`() {
        When {
            get("/v1/providers")
        } Then {
            body("status", containsInAnyOrder("UNKNOWN", "UNKNOWN"))
        }
    }

    @Test
    fun `checks status when post providers`() {
        When {
            post("/v1/providers")
        }

        When {
            get("/v1/providers")
        } Then {
            body("status", everyItem(equalTo("DOWN")))
        }
    }
}

private fun aValidProvider() =
    Provider(
        name = ProviderName("ListR1 Test"),
        url = URI("http://test.com"),
        status = ProviderStatus.UP,
    )
