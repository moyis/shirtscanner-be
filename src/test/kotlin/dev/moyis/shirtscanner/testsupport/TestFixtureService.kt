package dev.moyis.shirtscanner.testsupport

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.stereotype.Component

@Component
class TestFixtureService(
    private val providerRepository: ProviderRepository,
) {
    fun clearAll() {
        providerRepository.deleteAll()
    }

    fun persistProviderData(vararg data: ProviderData) {
        providerRepository.saveAll(data.toList())
    }
}
