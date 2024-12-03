package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository

class ProviderService(
    private val productProviders: List<ProductProvider>,
    private val providerRepository: ProviderRepository,
) {
    fun findAll() =
        providerRepository.findAll()
            .takeUnless { it.isEmpty() }
            ?: productProviders.map {
                ProviderData(
                    url = it.url,
                    name = it.name,
                    status = ProviderStatus.UNKNOWN,
                )
            }

    fun checkStatus(): Int {
        val providerDataList =
            productProviders.map {
                ProviderData(
                    url = it.url,
                    name = it.name,
                    status = it.status(),
                )
            }
        return providerRepository.saveAll(providerDataList)
    }
}
