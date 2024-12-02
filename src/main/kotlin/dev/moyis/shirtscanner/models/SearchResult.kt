package dev.moyis.shirtscanner.models

data class SearchResult(
    val queryUrl: String,
    val products: List<Product>,
)
