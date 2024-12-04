package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.net.URI

@ConfigurationProperties("fetchers", ignoreUnknownFields = true)
data class FetchersConfigurationProperties(
    @NestedConfigurationProperty
    val listR1: List<ProviderDataConfigurationProperties>,
    @NestedConfigurationProperty
    val yupoo: List<ProviderDataConfigurationProperties>,
)

data class ProviderDataConfigurationProperties(
    val url: URI,
    val name: String,
)
