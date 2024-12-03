package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import java.net.URI

@RegisterReflectionForBinding
data class ProviderData(
    val url: URI,
    val name: ProviderName,
    val status: ProviderStatus,
)

@RegisterReflectionForBinding
@JvmInline
value class ProviderName(val value: String)