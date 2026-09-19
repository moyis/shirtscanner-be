package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.CurlFetcherConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.services.CurlImpersonatingFetcher
import dev.moyis.shirtscanner.infrastructure.services.CurlProcessRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CurlFetcherConfiguration(
    private val curlFetcherConfigurationProperties: CurlFetcherConfigurationProperties,
) {
    @Bean
    fun curlImpersonatingFetcher(): CurlImpersonatingFetcher =
        CurlImpersonatingFetcher(curlFetcherConfigurationProperties, CurlProcessRunner())
}