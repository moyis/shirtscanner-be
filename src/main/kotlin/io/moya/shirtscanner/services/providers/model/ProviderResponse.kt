package io.moya.shirtscanner.services.providers.model

data class ProviderResponse(
    val name: String,
    val status: ProviderStatus,
    val url: String,
)
