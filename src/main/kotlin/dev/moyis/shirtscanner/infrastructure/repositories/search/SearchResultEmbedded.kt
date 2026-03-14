package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.SearchResult
import java.net.URI

data class SearchResultEmbedded(
    val providerName: String,
    val queryUrl: String,
    val products: List<ProductEmbedded> = emptyList(),
) {
    fun toDomain() =
        SearchResult(
            providerName = providerName,
            queryUrl = URI(queryUrl),
            products = products.map { it.toDomain() },
        )

    companion object {
        fun from(searchResult: SearchResult) =
            SearchResultEmbedded(
                providerName = searchResult.providerName,
                queryUrl = searchResult.queryUrl.toString(),
                products = searchResult.products.map { ProductEmbedded.from(it) },
            )
    }
}
