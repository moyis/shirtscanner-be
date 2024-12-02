package dev.moyis.shirtscanner.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("fetchers", ignoreUnknownFields = true)
data class FetchersConfigurationProperties(
    @NestedConfigurationProperty
    val listR1: List<ProviderData>,
    @NestedConfigurationProperty
    val yupoo: List<ProviderData>,
)

data class ProviderData(
    val url: String,
    val name: String,
)
