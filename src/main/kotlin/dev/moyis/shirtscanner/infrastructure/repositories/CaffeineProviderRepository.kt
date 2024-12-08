package dev.moyis.shirtscanner.infrastructure.repositories

import com.github.benmanes.caffeine.cache.Caffeine
import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class CaffeineProviderRepository : ProviderRepository {
    private val cache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<ProviderName, Provider>()

    override fun saveAll(providers: List<Provider>) {
        val providersByName = providers.associateBy { it.name }
        cache.invalidateAll()
        cache.putAll(providersByName)
    }

    override fun findAll(): List<Provider> = cache.asMap().values.toList()

    override fun deleteAll() {
        cache.invalidateAll()
    }
}
