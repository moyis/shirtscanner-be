package dev.moyis.shirtscanner.infrastructure.repositories.search

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("search_results")
@CompoundIndex(name = "cmp-idx-provider-query", def = "{'providerName': 1, 'query': 1}")
data class SearchResultDocument(
    val providerName: String,
    val query: String,
    val searchResult: SearchResultEmbedded,
    val createdAt: LocalDateTime,
)
