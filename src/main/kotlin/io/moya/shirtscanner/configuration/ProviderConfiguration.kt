package io.moya.shirtscanner.configuration

import io.moya.shirtscanner.services.cache.CacheService
import io.moya.shirtscanner.services.fetchers.DefaultFetcher
import io.moya.shirtscanner.services.providers.ProductProvider
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val LOG = KotlinLogging.logger { }

@Configuration
class ProviderConfiguration(
    private val config: ProviderConfigurationProperties,
    private val defaultFetcher: DefaultFetcher,
    private val cacheService: CacheService,
) {

    @Bean
    fun providers(): List<ProductProvider> {
        val defaultFetcherProviders = defaultFetcherProviders()
        return defaultFetcherProviders
    }

    private fun defaultFetcherProviders(): List<ProductProvider> {
        val providers = config.default.map { ProductProvider(defaultFetcher, it, cacheService) }
        LOG.info { "Found ${providers.size} providers using DefaultFetcher::class" }
        return providers
    }
}


@ConfigurationProperties("providers", ignoreUnknownFields = true)
data class ProviderConfigurationProperties(
    @NestedConfigurationProperty
    val default: List<ProviderData>,
)

data class ProviderData(
    val url: String,
    val name: String,
)
