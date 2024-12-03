package dev.moyis.shirtscanner.domain.spi

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.model.SearchResult

interface ProductProvider {
    fun search(query: String): SearchResult

    fun providerData(): ProviderData

    fun status(): ProviderStatus
}
