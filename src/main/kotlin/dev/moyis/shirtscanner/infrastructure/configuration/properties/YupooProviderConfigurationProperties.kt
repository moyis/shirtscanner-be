package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties("fetchers.configuraiton.yupoo")
data class YupooProviderConfigurationProperties(
    val imageProxyHost: URI,
)
