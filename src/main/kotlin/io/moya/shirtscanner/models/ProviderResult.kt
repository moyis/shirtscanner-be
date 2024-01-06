package io.moya.shirtscanner.models

data class ProviderResult(
    val providerName: String,
    val queryUrl: String,
    val products: List<Product>,
)
