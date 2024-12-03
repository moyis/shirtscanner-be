package dev.moyis.shirtscanner.infrastructure.controllers.model

import dev.moyis.shirtscanner.domain.model.SearchResult
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class SearchResultResponse(
    val providerName: String,
    val queryUrl: String,
    val products: List<ProductResponse>,
) {
    companion object {
        fun fromSearchResult(searchResult: SearchResult) =
            with(searchResult) {
                SearchResultResponse(
                    providerName = providerName,
                    queryUrl = queryUrl.toString(),
                    products = products.map { ProductResponse.fromProduct(it) },
                )
            }
    }
}
