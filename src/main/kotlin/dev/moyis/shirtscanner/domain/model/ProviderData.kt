package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import java.net.URI

data class Provider(
    val name: ProviderName,
    val url: URI,
    val status: ProviderStatus,
)

@RegisterReflectionForBinding
data class ProviderData(
    val url: URI,
    val name: ProviderName,
)

@RegisterReflectionForBinding
data class ProviderStatusInformation(
    val name: ProviderName,
    val status: ProviderStatus,
)

@RegisterReflectionForBinding
@JvmInline
value class ProviderName(val value: String)
