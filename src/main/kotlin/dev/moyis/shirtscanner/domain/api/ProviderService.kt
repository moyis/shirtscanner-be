package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository

class ProviderService(
    private val productProviders: List<ProductProvider>,
    private val providerRepository: ProviderRepository,
) {
    fun findAll(): List<Provider> {
        val providerByName = providerRepository.findAll().associateBy { it.name }
        return productProviders.map {
            val providerData = it.providerData()
            providerByName[providerData.name] ?: Provider(
                name = providerData.name,
                url = providerData.url,
                status = ProviderStatus.UNKNOWN,
            )
        }
    }

    fun checkStatus() {
        val providerDataList =
            productProviders.map {
                val providerData = it.providerData()
                Provider(
                    name = providerData.name,
                    url = providerData.url,
                    status = it.status(),
                )
            }
        providerRepository.saveAll(providerDataList)
    }
}
