package dev.moyis.shirtscanner.testsupport

import dev.moyis.shirtscanner.services.providers.model.ProviderStatus
import org.springframework.stereotype.Component

@Component
class TestFixtureService {
    fun clearAll() {
    }

    fun persistStatus(
        providerName: String,
        status: ProviderStatus,
    ) {
    }
}
