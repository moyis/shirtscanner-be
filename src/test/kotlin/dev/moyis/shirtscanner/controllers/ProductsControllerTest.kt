package dev.moyis.shirtscanner.controllers

import dev.moyis.shirtscanner.models.ProviderResult
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductsControllerTest : AbstractIntegrationTest() {
    @Test
    fun `search products returns 200`() {
        Given {
            queryParam("q", "argentina")
        } When {
            get("/v1/products")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `search products with no query param returns 400`() {
        When {
            get("/v1/products")
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun `search providers values for each provider configured`() {
        val providerResults =
            Given {
                queryParam("q", "argentina")
            } When {
                get("/v1/products")
            } Extract {
                body().jsonPath().getList("", ProviderResult::class.java)
            }
        assertThat(providerResults).extracting<String> { it.providerName }.containsExactlyInAnyOrder("ListR1 Test", "Yupoo Test")
    }

    @Test
    fun `search providers maps products for each provider configured`() {
        val providerResults =
            Given {
                queryParam("q", "argentina")
            } When {
                get("/v1/products")
            } Extract {
                body().jsonPath().getList("", ProviderResult::class.java)
            }
        assertThat(providerResults).satisfiesExactlyInAnyOrder(
            { assertThat(it.products).hasSize(100) },
            { assertThat(it.products).hasSize(38) },
        )
    }
}
