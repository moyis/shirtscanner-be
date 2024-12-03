package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cors")
data class CorsConfigurationProperties(
    val allowedOrigins: List<String>,
)
