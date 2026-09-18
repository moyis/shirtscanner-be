package dev.moyis.shirtscanner.infrastructure.repositories.providers

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.dao.DataAccessResourceFailureException
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class MongoDbProviderRepositoryFallbackTest {
    private val providerMongoRepository = mock<ProviderMongoRepository>()
    private val clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val repository = MongoDbProviderRepository(providerMongoRepository, clock)

    @Nested
    inner class WhenMongoIsUnavailable {
        @Test
        fun `findAll returns an empty list`() {
            whenever(providerMongoRepository.findAll()).thenThrow(mongoFailure())

            assertThat(repository.findAll()).isEmpty()
        }

        @Test
        fun `saveAll swallows the failure`() {
            whenever(providerMongoRepository.saveAll(any<Iterable<ProviderDocument>>())).thenThrow(mongoFailure())

            repository.saveAll(listOf(aProvider()))
        }

        @Test
        fun `deleteAll swallows the failure`() {
            whenever(providerMongoRepository.deleteAll()).thenThrow(mongoFailure())

            repository.deleteAll()
        }
    }

    private fun mongoFailure() = DataAccessResourceFailureException("boom", RuntimeException("connection refused"))

    private fun aProvider() =
        Provider(
            name = ProviderName("Provider"),
            url = URI("https://example.com"),
            status = ProviderStatus.UP,
        )
}