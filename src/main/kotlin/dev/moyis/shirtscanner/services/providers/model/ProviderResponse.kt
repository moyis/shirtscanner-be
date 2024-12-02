package dev.moyis.shirtscanner.services.providers.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class ProviderResponse(
    val name: String,
    val status: ProviderStatus,
    val url: String,
)
