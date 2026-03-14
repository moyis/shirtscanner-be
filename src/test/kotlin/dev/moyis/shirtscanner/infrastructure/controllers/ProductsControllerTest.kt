package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.model.SearchResultEvent
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.test.runTest
import org.springframework.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.returnResult
import reactor.test.StepVerifier
import java.time.Duration

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
                statusCode(HttpStatus.OK.value())
            }
        }

        @Test
        fun `return provider names for each result`() {
            Given {
                queryParam("q", "argentina")
            } When {
                get("/v1/products")
            } Then {
                body("providerName", containsInAnyOrder("ListR1 Test", "Yupoo Test"))
            }
        }

        @Test
        fun `return products for each provider`() {
            Given {
                queryParam("q", "argentina")
            } When {
                get("/v1/products")
            } Then {
                body("products.collect { it.size() }", containsInAnyOrder(100, 38))
            }
        }

        @Test
        fun `return 400 when no search param is sent`() {
            When {
                get("/v1/products")
            } Then {
                statusCode(HttpStatus.BAD_REQUEST.value())
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
            await().atMost(Duration.ofSeconds(3)).untilAsserted {
                webTestClient
                    .get()
                    .uri { it.path("/v1/products/stream").queryParam("q", "argentina").build() }
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchange()
                    .returnResult<SearchResultEvent>()
                    .responseBody
                    .map { it.total }
                    .`as`(StepVerifier::create)
                    .expectNextCount(2)
                    .expectComplete()
                    .verify(Duration.ofSeconds(10))
            }
        }

        @Test
        fun `return 400 when no search param is sent`() {
            When {
                get("/v1/products/stream")
            } Then {
                statusCode(HttpStatus.BAD_REQUEST.value())
            }
        }
    }
}
