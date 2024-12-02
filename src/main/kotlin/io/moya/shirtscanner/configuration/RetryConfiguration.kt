package io.moya.shirtscanner.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.support.RetryTemplate
import java.time.Duration

@Configuration
class RetryConfiguration(
    private val configuration: RetryConfigurationProperties,
) {
    @Bean
    fun imageProxyRetryTemplate(): RetryTemplate =
        with(configuration.imageProxy) {
            RetryTemplate
                .builder()
                .maxAttempts(maxAttempts)
                .build()
        }

    @Bean
    fun webConnectorRetryTemplate(): RetryTemplate =
        with(configuration.webConnector) {
            RetryTemplate
                .builder()
                .exponentialBackoff(initialInterval, multiplier, maxInterval)
                .maxAttempts(maxAttempts)
                .build()
        }
}

@ConfigurationProperties("retry")
data class RetryConfigurationProperties(
    @NestedConfigurationProperty
    val webConnector: ExponentialConfiguration,
    @NestedConfigurationProperty
    val imageProxy: SimpleConfiguration,
)

data class SimpleConfiguration(
    val maxAttempts: Int,
)

data class ExponentialConfiguration(
    val initialInterval: Duration,
    val multiplier: Double,
    val maxInterval: Duration,
    val maxAttempts: Int,
)
