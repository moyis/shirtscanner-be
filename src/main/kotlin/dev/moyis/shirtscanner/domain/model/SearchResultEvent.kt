package dev.moyis.shirtscanner.domain.model

data class SearchResultEvent(
    val total: Int,
    val data: SearchResult,
)
