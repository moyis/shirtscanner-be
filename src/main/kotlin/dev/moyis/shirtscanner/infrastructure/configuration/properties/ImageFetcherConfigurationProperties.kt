package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties("image-fetcher")
data class ImageFetcherConfigurationProperties(
    val baseUrl: URI,
    val referer: URI,
    val maxRetries: Long,
    val retryDelay: Duration,
)
