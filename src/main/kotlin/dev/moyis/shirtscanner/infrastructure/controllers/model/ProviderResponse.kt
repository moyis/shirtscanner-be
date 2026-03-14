package dev.moyis.shirtscanner.infrastructure.controllers.model

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderStatus

data class ProviderResponse(
    val url: String,
    val name: String,
    val status: ProviderStatus,
) {
    companion object {
        fun fromProvider(provider: Provider): ProviderResponse =
            with(provider) {
                ProviderResponse(
                    url = url.toString(),
                    name = name.value,
                    status = status,
                )
            }
    }
}
