package io.moya.shirtscanner.services.providers

import io.moya.shirtscanner.configuration.ProviderMetadata
import io.moya.shirtscanner.models.SearchResult
import io.moya.shirtscanner.services.cache.CacheService
import io.moya.shirtscanner.services.fetchers.ProductsFetcher
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductProviderTest {

    private val productsFetcher = EmptyProductFetcher("localhost:9999/search%s")

    private val providerName: String = "provider"

    private val providerMetadata: ProviderMetadata = ProviderMetadata(providerName)

    private val cacheService = MockCacheService()

    private lateinit var subject: ProductProvider

    @BeforeEach
    fun setUp() {
        subject = ProductProvider(productsFetcher, providerMetadata, cacheService)
    }

    @Test
    fun `product provider returns provider metadata`() {
        val q = "anything"
        val result = subject.search(q)
        AssertionsForClassTypes.assertThat(result.providerName).isEqualTo(providerName)
    }
}

private class MockCacheService : CacheService {
    override fun <T> getAndSetIfAbsent(key: String, provider: () -> T) = provider.invoke()
}

private class EmptyProductFetcher(private val url: String) : ProductsFetcher {
    override fun search(q: String) = SearchResult(
        queryUrl = url,
        products = emptyList()
    )
}