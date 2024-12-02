package dev.moyis.shirtscanner.services.providers

import dev.moyis.shirtscanner.configuration.ProviderData
import dev.moyis.shirtscanner.services.providers.model.ProviderResponse
import dev.moyis.shirtscanner.services.providers.model.ProviderStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val LOG = KotlinLogging.logger { }

@Service
class ProviderService(
    private val providers: List<ProviderData>,
) {
    fun getProviders(): List<ProviderResponse> {
        return providers.map {
            ProviderResponse(
                name = it.name,
                status = ProviderStatus.UNKNOWN,
                url = it.url,
            )
        }
    }

    fun checkStatus() = LOG.info { "Checking providers status" }
}
