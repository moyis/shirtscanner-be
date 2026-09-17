package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.CorsConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class CorsConfiguration(
    private val corsConfigurationProperties: CorsConfigurationProperties,
) : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(*corsConfigurationProperties.allowedOrigins.toTypedArray())
    }
}
