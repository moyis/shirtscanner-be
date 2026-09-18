package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.SearchResultRepository
import mu.KotlinLogging
import org.springframework.dao.DataAccessException
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime

private val LOG = KotlinLogging.logger {}

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
        try {
            val cached = searchResultMongoDbRepository.findById(id).orElse(null)
            if (cached != null) return cached.searchResult.toDomain()
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; skipping search-result cache read" }
            return fn()
        }

        val computed = fn()
        try {
            searchResultMongoDbRepository.insert(document(providerName.value, query, computed))
        } catch (e: DuplicateKeyException) {
            return try {
                searchResultMongoDbRepository.findById(id).map { it.searchResult.toDomain() }.orElse(computed)
            } catch (readError: DataAccessException) {
                LOG.warn(readError) { "MongoDB unavailable after insert race; serving computed result" }
                computed
            }
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; serving computed result uncached" }
        }
        return computed
    }

    override fun save(
        providerName: ProviderName,
        query: String,
        searchResult: SearchResult,
    ) {
        try {
            searchResultMongoDbRepository.save(document(providerName.value, query, searchResult))
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; skipping search-result cache save" }
        }
    }

    override fun deleteAll() {
        try {
            searchResultMongoDbRepository.deleteAll()
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; skipping search-result cache clear" }
        }
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