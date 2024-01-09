package io.moya.shirtscanner.services.providers

import io.moya.shirtscanner.configuration.ProviderData
import io.moya.shirtscanner.models.ProviderResult
import io.moya.shirtscanner.services.cache.CacheService
import io.moya.shirtscanner.services.fetchers.ProductsFetcher
import mu.KotlinLogging
import kotlin.time.measureTimedValue

private val LOG = KotlinLogging.logger { }

class ProductProvider(
    private val productsFetcher: ProductsFetcher,
    private val providerData: ProviderData,
    private val cacheService: CacheService,
) {
    fun search(q: String) = cacheService.computeIfAbsent(q.uppercase()) { doSearch(q) }

    private fun doSearch(q: String): ProviderResult {
        LOG.info { "Starting search for provider [${providerData.name}] and query [$q]" }
        val (searchResult, duration) = measureTimedValue { productsFetcher.search(q, providerData.url) }
        LOG.info { "Finished search for provider [${providerData.name}] and query [$q] with ${searchResult.products.size} products and took ${duration.inWholeMilliseconds} ms" }
        return ProviderResult(
            providerName = providerData.name,
            queryUrl = searchResult.queryUrl,
            products = searchResult.products
        )
    }
}