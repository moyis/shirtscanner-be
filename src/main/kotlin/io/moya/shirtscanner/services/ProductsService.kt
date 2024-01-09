package io.moya.shirtscanner.services

import io.moya.shirtscanner.models.ProviderResult
import io.moya.shirtscanner.services.cache.CacheService
import io.moya.shirtscanner.services.providers.ProductProvider
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class ProductsService(
    private val providers: List<ProductProvider>,
) {
    private val executorService = Executors.newVirtualThreadPerTaskExecutor()

    fun search(q: String) = providers
        .map { executorService.submit<ProviderResult> { it.search(q) } }
        .map { it.get() }
        .sortedByDescending { it.products.size }
        .toList()
}
