package dev.moyis.shirtscanner.testsupport

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import dev.moyis.shirtscanner.domain.spi.SearchResultRepository
import org.springframework.stereotype.Component

@Component
class TestFixtureService(
    private val providerRepository: ProviderRepository,
    private val searchResultRepository: SearchResultRepository,
) {
    fun clearAll() {
        providerRepository.deleteAll()
        searchResultRepository.deleteAll()
    }

    fun persistProviderData(vararg data: Provider) {
        providerRepository.saveAll(data.toList())
    }

    fun persistSearchResult(
        providerName: ProviderName,
        query: String,
        searchResult: SearchResult,
    ) {
        searchResultRepository.save(providerName, query, searchResult)
    }
}
