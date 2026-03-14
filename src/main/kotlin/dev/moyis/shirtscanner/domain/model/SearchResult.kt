package dev.moyis.shirtscanner.domain.model

import java.net.URI

data class SearchResult(
    val providerName: String,
    val queryUrl: URI,
    val products: List<Product> = emptyList(),
)
