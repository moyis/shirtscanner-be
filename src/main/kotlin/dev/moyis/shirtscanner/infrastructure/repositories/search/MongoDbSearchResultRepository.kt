package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.SearchResultRepository
import org.springframework.dao.DuplicateKeyException
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
        val id = documentId(providerName.value, query)
        val cached = searchResultMongoDbRepository.findById(id).orElse(null)
        if (cached != null) return cached.searchResult.toDomain()

        val computed = fn()
        return try {
            searchResultMongoDbRepository.insert(document(providerName.value, query, computed))
            computed
        } catch (e: DuplicateKeyException) {
            searchResultMongoDbRepository.findById(id).map { it.searchResult.toDomain() }.orElse(computed)
        }
    }

    override fun save(
        providerName: ProviderName,
        query: String,
        searchResult: SearchResult,
    ) {
        searchResultMongoDbRepository.save(document(providerName.value, query, searchResult))
    }

    override fun deleteAll() {
        searchResultMongoDbRepository.deleteAll()
    }

    private fun document(
        providerName: String,
        query: String,
        searchResult: SearchResult,
    ) =
        SearchResultDocument.create(
            providerName = providerName,
            query = query,
            searchResult = SearchResultEmbedded.from(searchResult),
            createdAt = LocalDateTime.now(clock),
        )

    private fun documentId(
        providerName: String,
        query: String,
    ) = "$providerName:$query"
}