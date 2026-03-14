package dev.moyis.shirtscanner.domain.model

import java.net.URI

data class Provider(
    val name: ProviderName,
    val url: URI,
    val status: ProviderStatus,
)

data class ProviderData(
    val url: URI,
    val name: ProviderName,
)

@JvmInline
value class ProviderName(
    val value: String,
) {
    override fun toString(): String = value
}
