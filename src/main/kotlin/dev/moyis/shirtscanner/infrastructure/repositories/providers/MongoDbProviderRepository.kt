package dev.moyis.shirtscanner.infrastructure.repositories.providers

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import mu.KotlinLogging
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime

private val LOG = KotlinLogging.logger {}

@Repository
class MongoDbProviderRepository(
    private val providerMongoRepository: ProviderMongoRepository,
    private val clock: Clock,
) : ProviderRepository {
    override fun saveAll(providers: List<Provider>) {
        try {
            val now = LocalDateTime.now(clock)
            providerMongoRepository.saveAll(providers.map { ProviderDocument.from(it, now) })
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; skipping provider status save" }
        }
    }

    override fun findAll(): List<Provider> =
        try {
            providerMongoRepository.findAll().map { it.toDomain() }
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; reporting unknown provider statuses" }
            emptyList()
        }

    override fun deleteAll() {
        try {
            providerMongoRepository.deleteAll()
        } catch (e: DataAccessException) {
            LOG.warn(e) { "MongoDB unavailable; skipping provider clear" }
        }
    }
}
