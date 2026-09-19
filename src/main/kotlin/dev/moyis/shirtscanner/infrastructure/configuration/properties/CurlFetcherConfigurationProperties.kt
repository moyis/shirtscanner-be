package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("fetchers.curl")
data class CurlFetcherConfigurationProperties(
    val binary: String = "/usr/local/bin/curl_chrome120",
    val defaultTimeout: Duration = Duration.ofSeconds(20),
    val attempts: Int = 5,
    val backoff: Duration = Duration.ofMillis(500),
)