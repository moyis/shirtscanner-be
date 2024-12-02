package dev.moyis.shirtscanner.services.fetchers

import dev.moyis.shirtscanner.models.SearchResult

interface ProductsFetcher {
    fun search(
        q: String,
        url: String,
    ): SearchResult
}
