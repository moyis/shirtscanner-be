package io.moya.shirtscanner.configuration

import io.moya.shirtscanner.services.fetchers.DefaultFetcher
import io.moya.shirtscanner.services.ProductProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [ProviderConfigurationProperties::class])
class ProviderConfiguration(
    private val providerConfigurationProperties: ProviderConfigurationProperties,
) {

    @Bean
    fun fiveBoundlessFetcher() = with(providerConfigurationProperties.fiveBoundless) {
        ProductProvider(DefaultFetcher(url), metadata)
    }

    @Bean
    fun grkitsFetcher() = with(providerConfigurationProperties.grkits) {
        ProductProvider(DefaultFetcher(url), metadata)
    }

    @Bean
    fun kitsggFetcher() = with(providerConfigurationProperties.kitsgg) {
        ProductProvider(DefaultFetcher(url), metadata)
    }

    @Bean
    fun aclotzoneFetcher() = with(providerConfigurationProperties.aclotzone) {
        ProductProvider(DefaultFetcher(url), metadata)
    }

    @Bean
    fun fofoshopFetcher() = with(providerConfigurationProperties.fofoshop) {
        ProductProvider(DefaultFetcher(url), metadata)
    }
}


@ConfigurationProperties("providers")
data class ProviderConfigurationProperties(
    @NestedConfigurationProperty
    val fiveBoundless: ProviderData,
    @NestedConfigurationProperty
    val kitsgg: ProviderData,
    @NestedConfigurationProperty
    val grkits: ProviderData,
    @NestedConfigurationProperty
    val aclotzone: ProviderData,
    @NestedConfigurationProperty
    val fofoshop: ProviderData,
)

data class ProviderData(
    val url: String,
    val cacheKey: String,
    @NestedConfigurationProperty
    val metadata: ProviderMetadata,
)

data class ProviderMetadata(
    val name: String,
)
