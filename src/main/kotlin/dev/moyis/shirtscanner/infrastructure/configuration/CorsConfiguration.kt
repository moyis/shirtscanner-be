package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.CorsConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration(
    private val corsConfigurationProperties: CorsConfigurationProperties,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(*corsConfigurationProperties.allowedOrigins.toTypedArray())
    }
}
