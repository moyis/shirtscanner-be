package io.moya.shirtscanner.services.fetchers

import io.moya.shirtscanner.models.SearchResult

interface ProductsFetcher {
    fun search(q: String): SearchResult
}