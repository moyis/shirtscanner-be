package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
enum class ProviderStatus {
    UP,
    DOWN,
    UNKNOWN,
}