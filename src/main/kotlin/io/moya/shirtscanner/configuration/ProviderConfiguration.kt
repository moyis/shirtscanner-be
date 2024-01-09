package io.moya.shirtscanner.configuration

import io.moya.shirtscanner.services.cache.CacheService
import io.moya.shirtscanner.services.fetchers.ListR1Fetcher
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
    private val listR1Fetcher: ListR1Fetcher,
    private val cacheService: CacheService,
) {

    @Bean
    fun providers(): List<ProductProvider> {
        val defaultFetcherProviders = defaultFetcherProviders()
        return defaultFetcherProviders
    }

    private fun defaultFetcherProviders(): List<ProductProvider> {
        val providers = config.listR1.map { ProductProvider(listR1Fetcher, it, cacheService) }
        LOG.info { "Found ${providers.size} providers using ListR1 fetcher" }
        return providers
    }
}


@ConfigurationProperties("providers", ignoreUnknownFields = true)
data class ProviderConfigurationProperties(
    @NestedConfigurationProperty
    val listR1: List<ProviderData>,
)

data class ProviderData(
    val url: String,
    val name: String,
)
