package dev.moyis.shirtscanner.domain.spi

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult

interface SearchResultRepository {
    fun computeIfAbsent(
        providerName: ProviderName,
        query: String,
        fn: () -> SearchResult,
    ): SearchResult

    fun save(
        providerName: ProviderName,
        query: String,
        searchResult: SearchResult,
    )

    fun deleteAll()
}
