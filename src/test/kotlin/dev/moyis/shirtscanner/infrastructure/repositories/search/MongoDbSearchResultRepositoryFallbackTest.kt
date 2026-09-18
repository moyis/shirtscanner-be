package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.dao.DuplicateKeyException
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional

class MongoDbSearchResultRepositoryFallbackTest {
    private val searchResultMongoDbRepository = mock<SearchResultMongoDbRepository>()
    private val clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val repository = MongoDbSearchResultRepository(searchResultMongoDbRepository, clock)

    @Nested
    inner class WhenCacheReadFails {
        @Test
        fun `serves the computed result without storing it`() {
            whenever(searchResultMongoDbRepository.findById(any())).thenThrow(mongoFailure())

            var providerCalls = 0
            val result = repository.computeIfAbsent(ProviderName("Provider"), "query") {
                providerCalls++
                aSearchResult("Provider")
            }

            assertThat(result.providerName).isEqualTo("Provider")
            assertThat(providerCalls).isEqualTo(1)
            verify(searchResultMongoDbRepository, never()).insert(any<SearchResultDocument>())
        }
    }

    @Nested
    inner class WhenCacheStoreFails {
        @Test
        fun `serves the computed result despite the failed insert`() {
            whenever(searchResultMongoDbRepository.findById(any())).thenReturn(Optional.empty())
            whenever(searchResultMongoDbRepository.insert(any<SearchResultDocument>()))
                .thenThrow(mongoFailure())

            var providerCalls = 0
            val result = repository.computeIfAbsent(ProviderName("Provider"), "query") {
                providerCalls++
                aSearchResult("Provider")
            }

            assertThat(result.providerName).isEqualTo("Provider")
            assertThat(providerCalls).isEqualTo(1)
        }

        @Test
        fun `serves the computed result when the insert-race re-read fails`() {
            whenever(searchResultMongoDbRepository.findById(any()))
                .thenReturn(Optional.empty())
                .thenThrow(mongoFailure())
            whenever(searchResultMongoDbRepository.insert(any<SearchResultDocument>()))
                .thenThrow(DuplicateKeyException("dup", RuntimeException("dup")))

            var providerCalls = 0
            val result = repository.computeIfAbsent(ProviderName("Provider"), "query") {
                providerCalls++
                aSearchResult("Provider")
            }

            assertThat(result.providerName).isEqualTo("Provider")
            assertThat(providerCalls).isEqualTo(1)
        }
    }

    @Nested
    inner class WhenMongoIsUnavailable {
        @Test
        fun `save swallows the failure`() {
            whenever(searchResultMongoDbRepository.save(any<SearchResultDocument>())).thenThrow(mongoFailure())

            repository.save(ProviderName("Provider"), "query", aSearchResult("Provider"))
        }

        @Test
        fun `deleteAll swallows the failure`() {
            whenever(searchResultMongoDbRepository.deleteAll()).thenThrow(mongoFailure())

            repository.deleteAll()
        }
    }

    private fun mongoFailure() = DataAccessResourceFailureException("boom", RuntimeException("connection refused"))

    private fun aSearchResult(providerName: String) =
        SearchResult(
            providerName = providerName,
            queryUrl = URI("https://example.com/search?q=query"),
            products = emptyList(),
        )
}