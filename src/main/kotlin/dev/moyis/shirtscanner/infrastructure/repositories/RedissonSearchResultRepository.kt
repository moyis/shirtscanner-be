package dev.moyis.shirtscanner.infrastructure.repositories

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.SearchResultRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository
import java.time.Duration

private val DEFAULT_TTL = Duration.ofDays(1)

@Repository
class RedissonSearchResultRepository(
    private val redisson: RedissonClient,
) : SearchResultRepository {
    override fun computeIfAbsent(
        providerName: ProviderName,
        query: String,
        fn: () -> SearchResult,
    ): SearchResult {
        val searchResultBucket = redisson.getBucket<SearchResult>("results-${providerName.value}-$query")
        return searchResultBucket.get()
            ?: fn.invoke().also { searchResultBucket.set(it, DEFAULT_TTL) }
    }

    override fun save(
        providerName: ProviderName,
        query: String,
        searchResult: SearchResult,
    ) {
        redisson
            .getBucket<SearchResult>("results-${providerName.value}-$query")
            .set(searchResult, DEFAULT_TTL)
    }

    override fun deleteAll() {
        redisson.keys.deleteByPattern("results-*")
    }
}
