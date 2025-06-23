package dev.moyis.shirtscanner.infrastructure.repositories.providers

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime

@Repository
class MongoDbProviderRepository(
    private val providerMongoRepository: ProviderMongoRepository,
    private val clock: Clock,
) : ProviderRepository {
    override fun saveAll(providers: List<Provider>) {
        val now = LocalDateTime.now(clock)
        providerMongoRepository.saveAll(providers.map { ProviderDocument.from(it, now) })
    }

    override fun findAll() = providerMongoRepository.findAll().map { it.toDomain() }

    override fun deleteAll() = providerMongoRepository.deleteAll()
}
