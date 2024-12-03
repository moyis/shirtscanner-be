package dev.moyis.shirtscanner.domain.spi

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.model.SearchResult
import java.net.URI

interface ProductProvider {
    val url: URI
    val name: ProviderName

    fun search(query: String): SearchResult

    fun status(): ProviderStatus
}
