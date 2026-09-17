package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("search-result-cache")
data class SearchResultCacheConfigurationProperties(
    val ttl: Duration = Duration.ofHours(24),
)