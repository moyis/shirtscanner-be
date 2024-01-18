package io.moya.shirtscanner.services

import io.moya.shirtscanner.models.ProviderResult
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class ProviderResultEvent(
    val total: Int,
    val data: ProviderResult,
)
