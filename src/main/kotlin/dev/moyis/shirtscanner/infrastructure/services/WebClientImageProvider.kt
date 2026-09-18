package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.domain.spi.ImageProvider
import dev.moyis.shirtscanner.infrastructure.configuration.properties.ImageFetcherConfigurationProperties
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux
import reactor.util.retry.Retry

@Service
class WebClientImageProvider(
    config: ImageFetcherConfigurationProperties,
) : ImageProvider {
    private val webClient = WebClient.create()
    private val baseUrl = config.baseUrl
    private val referrer = config.referer
    private val retry = Retry.fixedDelay(config.maxRetries, config.retryDelay)

    override fun get(path: String): Flux<DataBuffer> =
        webClient
            .get()
            .uri("$baseUrl/$path")
            .header(HttpHeaders.REFERER, "$referrer")
            .retrieve()
            .bodyToFlux(DataBuffer::class.java)
            .retryWhen(retry)
            .onErrorComplete()
}
