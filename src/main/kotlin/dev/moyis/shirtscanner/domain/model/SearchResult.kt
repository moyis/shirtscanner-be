package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import java.net.URI

@RegisterReflectionForBinding
data class SearchResult(
    val providerName: String,
    val queryUrl: URI,
    val products: List<Product> = emptyList(),
)
