package dev.moyis.shirtscanner.infrastructure.repositories

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository

private const val PROVIDERS = "providers"

@Repository
class RedissonProviderRepository(
    private val redisson: RedissonClient,
) : ProviderRepository {
    override fun saveAll(providers: List<Provider>) {
        val providersByName = providers.associateBy { it.name }
        redisson.getMap<ProviderName, Provider>(PROVIDERS).putAll(providersByName)
    }

    override fun findAll(): List<Provider> = redisson.getMap<ProviderName, Provider>(PROVIDERS).values.toList()

    override fun deleteAll() {
        redisson.getMap<ProviderName, Provider>(PROVIDERS).clear()
    }
}
