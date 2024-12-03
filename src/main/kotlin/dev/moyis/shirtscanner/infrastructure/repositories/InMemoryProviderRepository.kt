package dev.moyis.shirtscanner.infrastructure.repositories

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryProviderRepository : ProviderRepository {
    private var repository: Map<ProviderName, Provider> = emptyMap()

    override fun saveAll(providers: List<Provider>) {
        repository = providers.associateBy { it.name }
    }

    override fun findAll(): List<Provider> {
        return repository.values.toList()
    }

    override fun deleteAll() {
        repository = emptyMap()
    }
}
