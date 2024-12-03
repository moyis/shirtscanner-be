package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("document-fetcher")
data class DocumentFetcherConfigurationProperties(
    val defaultTimeout: Duration,
)
