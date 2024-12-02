package dev.moyis.shirtscanner.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("web-connector")
data class WebConnectorConfigurationProperties(
    val defaultTimeout: Duration,
)
