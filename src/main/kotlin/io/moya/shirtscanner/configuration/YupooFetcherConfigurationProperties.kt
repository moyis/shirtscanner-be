package io.moya.shirtscanner.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("fetchers.configuraiton.yupoo")
data class YupooFetcherConfigurationProperties(
    val imageProxyHost: String,
)
