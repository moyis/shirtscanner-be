package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.net.URI
import java.time.Duration

class ProductServiceTest {
    @Nested
    inner class ABlockingSearch {
        @Test
        fun `returns empty with no configured providers`() {
            val productService = ProductService(emptyList())

            val results = productService.search("any")

            assertThat(results).isEmpty()
        }

        @Test
        fun `returns a result for every configured provider`() {
            val productService = ProductService(listOf(FakeProvider, FakeProvider, FakeProvider, FakeProvider))

            val results = productService.search("any")

            assertThat(results).hasSize(4)
        }
    }

    @Nested
    inner class AReactiveSearch {
        @Test
        fun `returns empty with no configured providers`() {
            val productService = ProductService(emptyList())

            StepVerifier
                .create(productService.searchStream("any"))
                .expectNextCount(0)
                .expectComplete()
                .verify()
        }

        @Test
        fun `returns a result for every configured provider`() {
            val productService = ProductService(listOf(FakeProvider, FakeProvider, FakeProvider, FakeProvider))

            StepVerifier
                .create(productService.searchStream("any").doOnNext { println("Emitted $it") })
                .expectNextCount(4)
                .expectComplete()
                .verify(Duration.ofSeconds(10))
        }
    }
}

private object FakeProvider : ProductProvider {
    override fun search(query: String) =
        SearchResult(
            providerName = "FixedProvider",
            queryUrl = URI("https://example.com/search?q=$query"),
            products = emptyList(),
        )

    override fun providerData() =
        ProviderData(
            url = URI("https://example.com/search"),
            name = ProviderName("FixedProvider"),
        )

    override fun status() = ProviderStatus.UP
}
