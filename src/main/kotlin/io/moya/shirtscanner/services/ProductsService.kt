package io.moya.shirtscanner.services

import io.moya.shirtscanner.models.ProviderResult
import io.moya.shirtscanner.services.providers.ProductProvider
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

private const val PROVIDERS_EVENT_NAME = "providers"

@Service
class ProductsService(
    private val providers: List<ProductProvider>,
) {
    private val executorService = Executors.newVirtualThreadPerTaskExecutor()

    fun search(q: String) =
        providers
            .map { executorService.submit<ProviderResult> { it.search(q) } }
            .map { it.get() }
            .sortedByDescending { it.products.size }
            .toList()

    fun searchStream(q: String): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        executorService.submit { searchStream(q, emitter) }
        return emitter
    }

    private fun searchStream(
        q: String,
        emitter: SseEmitter,
    ) {
        val totalSent = AtomicInteger(0)
        val total = providers.size
        providers
            .map { executorService.submit { emitter.send(sseEventBuilder(data = it.search(q), processedCount = totalSent.incrementAndGet(), totalCount = total)) } }
            .forEach { it.get() }
        emitter.complete()
    }

    private fun sseEventBuilder(
        data: ProviderResult,
        processedCount: Int,
        totalCount: Int,
    ) = SseEmitter.event()
        .id("${UUID.randomUUID()}")
        .name(PROVIDERS_EVENT_NAME)
        .data(ProviderResultEvent(total = totalCount, processed = processedCount, data = data))
}

data class ProviderResultEvent(
    val processed: Int,
    val total: Int,
    val data: ProviderResult,
)
