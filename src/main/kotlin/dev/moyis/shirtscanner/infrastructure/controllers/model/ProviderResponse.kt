package dev.moyis.shirtscanner.infrastructure.controllers.model

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class ProviderResponse(
    val url: String,
    val name: String,
    val status: ProviderStatus,
) {
    companion object {
        fun fromProviderData(providerData: ProviderData) =
            with(providerData) {
                ProviderResponse(
                    url = url.toString(),
                    name = name.value,
                    status = status,
                )
            }
    }
}
