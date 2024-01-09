package io.moya.shirtscanner.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("fetchers")
data class FetcherConfigurationProperties(
    val defaultTimeout: Duration
)