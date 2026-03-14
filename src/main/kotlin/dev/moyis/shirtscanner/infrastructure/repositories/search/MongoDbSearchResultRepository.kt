package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.SearchResultRepository
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime

@Repository
class MongoDbSearchResultRepository(
    private val searchResultMongoDbRepository: SearchResultMongoDbRepository,
    private val clock: Clock,
) : SearchResultRepository {
    override fun computeIfAbsent(
        providerName: ProviderName,
        query: String,
        fn: () -> SearchResult,
    ): SearchResult {
        val document =
            searchResultMongoDbRepository.findByProviderNameAndQuery(providerName = providerName.value, query = query)
                ?: searchResultMongoDbRepository.save(
                    SearchResultDocument(
                        providerName = providerName.value,
                        query = query,
                        searchResult = SearchResultEmbedded.from(fn()),
                        createdAt = LocalDateTime.now(clock),
                    ),
                )
        return document.searchResult.toDomain()
    }

    override fun save(
        providerName: ProviderName,
        query: String,
        searchResult: SearchResult,
    ) {
        searchResultMongoDbRepository.save(
            SearchResultDocument(
                providerName = providerName.value,
                query = query,
                searchResult = SearchResultEmbedded.from(searchResult),
                createdAt = LocalDateTime.now(clock),
            ),
        )
    }

    override fun deleteAll() {
        searchResultMongoDbRepository.deleteAll()
    }
}
