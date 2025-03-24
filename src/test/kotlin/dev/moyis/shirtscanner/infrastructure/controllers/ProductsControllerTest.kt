package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.model.SearchResultEvent
import dev.moyis.shirtscanner.infrastructure.controllers.model.SearchResultResponse
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.test.runTest
import org.apache.http.HttpStatus.SC_BAD_REQUEST
import org.apache.http.HttpStatus.SC_OK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier

class ProductsControllerTest : AbstractIntegrationTest() {
    @Nested
    inner class ABlockingSearch {
        @Test
        fun `return 200`() {
            Given {
                queryParam("q", "argentina")
            } When {
                get("/v1/products")
            } Then {
                statusCode(SC_OK)
            }
        }

        @Test
        fun `return provider names for each result`() {
            val providerResults =
                Given {
                    queryParam("q", "argentina")
                } When {
                    get("/v1/products")
                } Extract {
                    body().jsonPath().getList("", SearchResultResponse::class.java)
                }
            assertThat(providerResults)
                .extracting<String> { it.providerName }
                .containsExactlyInAnyOrder("ListR1 Test", "Yupoo Test")
        }

        @Test
        fun `return products for each provider`() {
            val providerResults =
                Given {
                    queryParam("q", "argentina")
                } When {
                    get("/v1/products")
                } Extract {
                    body().jsonPath().getList("", SearchResultResponse::class.java)
                }
            assertThat(providerResults).satisfiesExactlyInAnyOrder(
                { assertThat(it.products).hasSize(100) },
                { assertThat(it.products).hasSize(38) },
            )
        }

        @Test
        fun `return 400 when no search param is sent`() {
            When {
                get("/v1/products")
            } Then {
                statusCode(SC_BAD_REQUEST)
            }
        }
    }

    @Nested
    inner class AReactiveSearch {
        @Test
        fun `return 200`() {
            webTestClient
                .get()
                .uri { it.path("/v1/products/stream").queryParam("q", "argentina").build() }
                .exchange()
                .expectStatus()
                .isOk
        }

        @Test
        fun `return provider names for each result`() =
            runTest {
                val result =
                    webTestClient
                        .get()
                        .uri { it.path("/v1/products/stream").queryParam("q", "argentina").build() }
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .exchange()
                        .returnResult<SearchResultEvent>()
                        .responseBody
                        .map { it.data.providerName }
                        .asFlow()
                        .toList()

                assertThat(result).containsExactlyInAnyOrder("Yupoo Test", "ListR1 Test")
            }

        @Test
        fun `return products for each provider`() =
            runTest {
                val result =
                    webTestClient
                        .get()
                        .uri { it.path("/v1/products/stream").queryParam("q", "argentina").build() }
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .exchange()
                        .returnResult<SearchResultEvent>()
                        .responseBody
                        .map { it.data.products.size }
                        .asFlow()
                        .toList()

                assertThat(result).containsExactlyInAnyOrder(38, 100)
            }

        @Test
        fun `return number of configured providers in each message`() {
            webTestClient
                .get()
                .uri { it.path("/v1/products/stream").queryParam("q", "argentina").build() }
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult<SearchResultEvent>()
                .responseBody
                .map { it.total }
                .`as`(StepVerifier::create)
                .expectNext(2, 2)
                .verifyComplete()
        }

        @Test
        fun `return 400 when no search param is sent`() {
            When {
                get("/v1/products/stream")
            } Then {
                statusCode(SC_BAD_REQUEST)
            }
        }
    }
}
