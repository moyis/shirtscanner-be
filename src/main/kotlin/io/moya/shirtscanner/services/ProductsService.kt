package io.moya.shirtscanner.services

import io.moya.shirtscanner.models.ProviderResult
import io.moya.shirtscanner.services.providers.ProductProvider
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.Executors

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

    fun searchStream(q: String): Flux<ProviderResultEvent> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<ProviderResultEvent>()
        executorService.submit { searchStream(q, sink) }
        return sink.asFlux()
    }

    private fun searchStream(
        q: String,
        sink: Sinks.Many<ProviderResultEvent>,
    ) {
        providers
            .map { executorService.submit { sink.tryEmitNext(ProviderResultEvent(total = providers.size, data = it.search(q))) } }
            .forEach { it.get() }
        sink.tryEmitComplete()
    }
}
