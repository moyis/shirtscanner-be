package dev.moyis.shirtscanner.services.providers

import dev.moyis.shirtscanner.configuration.ProviderData
import dev.moyis.shirtscanner.models.SearchResult
import dev.moyis.shirtscanner.services.fetchers.ProductsFetcher
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductProviderTest {
    private val productsFetcher = EmptyProductFetcher()

    private val providerName: String = "provider"

    private val providerData = ProviderData("localhost:9999/search", providerName)

    private lateinit var subject: ProductProvider

    @BeforeEach
    fun setUp() {
        subject = ProductProvider(productsFetcher, providerData)
    }

    @Test
    fun `product provider returns provider metadata`() {
        val q = "anything"
        val result = subject.search(q)
        assertThat(result.providerName).isEqualTo(providerName)
    }
}

private class EmptyProductFetcher : ProductsFetcher {
    override fun search(
        q: String,
        url: String,
    ) = SearchResult(
        queryUrl = "$url/search?q=$q",
        products = listOf(),
    )
}
