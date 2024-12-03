package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class SearchResultEvent(
    val total: Int,
    val data: SearchResult,
)
