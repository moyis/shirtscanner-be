package dev.moyis.shirtscanner.infrastructure.repositories

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryProviderRepository : ProviderRepository {
    private var repository: Map<ProviderName, ProviderData> = emptyMap()

    override fun saveAll(providers: List<ProviderData>): Int {
        repository = providers.associateBy { it.name }
        return repository.size
    }

    override fun findAll(): List<ProviderData> = repository.values.toList()

    override fun deleteAll(): Int {
        val previousSize = repository.size
        repository = emptyMap()
        return previousSize
    }
}
