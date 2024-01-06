package io.moya.shirtscanner.services.providers

import io.moya.shirtscanner.configuration.ProviderMetadata
import io.moya.shirtscanner.models.ProviderResult
import io.moya.shirtscanner.services.fetchers.ProductsFetcher
import mu.KotlinLogging
import kotlin.time.measureTimedValue

private val LOG = KotlinLogging.logger { }

class ProductProvider(
    private val productsFetcher: ProductsFetcher,
    private val metadata: ProviderMetadata,
) {

    fun search(q: String) : ProviderResult {
        LOG.info { "Starting search for provider [${metadata.name}] and query [$q]" }
        val (searchResult, duration) = measureTimedValue { productsFetcher.search(q) }
        LOG.info { "Finished search for provider [${metadata.name}] and query [$q] with ${searchResult.products.size} products and took ${duration.inWholeMilliseconds} ms" }
        return ProviderResult(
            providerName = metadata.name,
            queryUrl = searchResult.queryUrl,
            products = searchResult.products
        )
    }
}