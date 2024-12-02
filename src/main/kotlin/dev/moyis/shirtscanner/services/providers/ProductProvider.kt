package dev.moyis.shirtscanner.services.providers

import dev.moyis.shirtscanner.configuration.ProviderData
import dev.moyis.shirtscanner.models.ProviderResult
import dev.moyis.shirtscanner.services.cache.CacheService
import dev.moyis.shirtscanner.services.fetchers.ProductsFetcher
import mu.KotlinLogging
import kotlin.time.measureTimedValue

private val LOG = KotlinLogging.logger { }

class ProductProvider(
    private val productsFetcher: ProductsFetcher,
    private val providerData: ProviderData,
    private val cacheService: CacheService,
) {
    fun search(q: String) = cacheService.computeIfAbsent("${providerData.name}_${q.uppercase()}") { doSearch(q) }

    private fun doSearch(q: String): ProviderResult {
        LOG.info { "Starting search for provider [${providerData.name}] and query [$q]" }
        val (searchResult, duration) = measureTimedValue { productsFetcher.search(q, providerData.url) }
        LOG.info { "Finished search for provider [${providerData.name}] and query [$q] with ${searchResult.products.size} products and took ${duration.inWholeMilliseconds} ms" }
        return ProviderResult(
            providerName = providerData.name,
            queryUrl = searchResult.queryUrl,
            products = searchResult.products,
        )
    }
}
