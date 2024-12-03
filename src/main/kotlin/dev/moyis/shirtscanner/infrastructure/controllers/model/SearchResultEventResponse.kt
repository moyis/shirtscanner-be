package dev.moyis.shirtscanner.infrastructure.controllers.model

import dev.moyis.shirtscanner.domain.model.SearchResultEvent

data class SearchResultEventResponse(
    val total: Int,
    val data: SearchResultResponse,
) {
    companion object {
        fun fromSearchResultResponse(searchResultEvent: SearchResultEvent) = with(searchResultEvent) {
            SearchResultEventResponse(
                total = total,
                data = SearchResultResponse.fromSearchResult(data),
            )
        }
    }
}