package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("fetchers.configuraiton.yupoo")
data class YupooProviderConfigurationProperties(
    val imageProxyHost: String,
)