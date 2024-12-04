package dev.moyis.shirtscanner.domain.spi

import dev.moyis.shirtscanner.domain.model.Provider

interface ProviderRepository {
    fun saveAll(providers: List<Provider>): Unit

    fun findAll(): List<Provider>

    fun deleteAll()
}
