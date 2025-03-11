package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.model.SearchResultEvent
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration
import java.util.concurrent.Executors

class ProductService(
    private val productProviders: List<ProductProvider>,
) {
    private val executorService = Executors.newVirtualThreadPerTaskExecutor()

    fun search(query: String): List<SearchResult> =
        productProviders
            .map { executorService.submit<SearchResult> { it.search(query) } }
            .map { it.get() }

    fun searchStream(query: String): Flux<SearchResultEvent> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<SearchResultEvent>()
        executorService.execute { search(query, sink) }
        return sink.asFlux()
    }

    private fun search(
        query: String,
        sink: Sinks.Many<SearchResultEvent>,
    ) {
        productProviders
            .map {
                executorService.submit {
                    sink.emitNext(SearchResultEvent(productProviders.size, it.search(query)))
                }
            }.map { it.get() }

        sink.emitComplete()
    }
}

fun <T> Sinks.Many<T & Any>.emitNext(next: T & Any) {
    emitNext(next, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)))
}

fun <T> Sinks.Many<T & Any>.emitComplete() {
    emitComplete(Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)))
}
