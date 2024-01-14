package io.moya.shirtscanner.services

import io.moya.shirtscanner.models.ProviderResult
import io.moya.shirtscanner.services.providers.ProductProvider
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

private val LOG = KotlinLogging.logger {  }
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

    private fun searchStream(q: String, emitter: SseEmitter) {
        val totalSent = AtomicInteger(0)
        val total = providers.size
        providers
            .map { executorService.submit<ProviderResult> { it.search(q) } }
            .forEach {
                val processed = totalSent.incrementAndGet()
                emitter.send(SseEmitter.event()
                    .id("${UUID.randomUUID()}")
                    .name("providers")
                    .data(ProviderResultEvent(total = total, processed = processed, data = it.get())))
            }
        emitter.complete()
    }
}

data class ProviderResultEvent(
    val processed: Int,
    val total: Int,
    val data: ProviderResult,
)