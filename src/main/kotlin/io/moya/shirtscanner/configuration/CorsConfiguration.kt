package io.moya.shirtscanner.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class CorsConfiguration(
    private val corsConfigurationProperties: CorsConfigurationProperties,
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*corsConfigurationProperties.allowedOrigins.toTypedArray())
    }
}

@ConfigurationProperties("cors")
data class CorsConfigurationProperties(
    val allowedOrigins: List<String>,
)