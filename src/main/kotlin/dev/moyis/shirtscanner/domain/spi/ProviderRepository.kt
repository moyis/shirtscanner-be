package dev.moyis.shirtscanner.domain.spi

import dev.moyis.shirtscanner.domain.model.ProviderData

interface ProviderRepository {
    fun saveAll(providers: List<ProviderData>): Int
    fun findAll(): List<ProviderData>
    fun deleteAll(): Int
}