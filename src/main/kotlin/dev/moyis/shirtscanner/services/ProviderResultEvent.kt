package dev.moyis.shirtscanner.services

import dev.moyis.shirtscanner.models.ProviderResult
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class ProviderResultEvent(
    val total: Int,
    val data: ProviderResult,
)
