package dev.moyis.shirtscanner.models

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class ProviderResult(
    val providerName: String,
    val queryUrl: String,
    val products: List<Product> = emptyList(),
)
