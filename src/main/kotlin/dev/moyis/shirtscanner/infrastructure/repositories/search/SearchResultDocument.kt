package dev.moyis.shirtscanner.infrastructure.repositories.search

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("search_results")
data class SearchResultDocument(
    @Id
    val id: String,
    val providerName: String,
    val query: String,
    val searchResult: SearchResultEmbedded,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun create(
            providerName: String,
            query: String,
            searchResult: SearchResultEmbedded,
            createdAt: LocalDateTime,
        ) =
            SearchResultDocument(
                id = "$providerName:$query",
                providerName = providerName,
                query = query,
                searchResult = searchResult,
                createdAt = createdAt,
            )
    }
}