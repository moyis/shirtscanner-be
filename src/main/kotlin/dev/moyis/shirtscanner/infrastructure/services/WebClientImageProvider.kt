package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.domain.spi.ImageProvider
import dev.moyis.shirtscanner.infrastructure.configuration.properties.ImageFetcherConfigurationProperties
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.util.retry.Retry
import java.time.Duration

@Service
class WebClientImageProvider(
    config: ImageFetcherConfigurationProperties,
) : ImageProvider {
    private val webClient = WebClient.create()
    private val baseUrl = config.baseUrl
    private val referrer = config.referer

    override suspend fun get(path: String): ByteArray? {
        return webClient
            .get()
            .uri("$baseUrl/$path")
            .header(HttpHeaders.REFERER, "$referrer")
            .retrieve()
            .bodyToMono<ByteArray>()
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .onErrorComplete()
            .awaitSingleOrNull()
    }
}
