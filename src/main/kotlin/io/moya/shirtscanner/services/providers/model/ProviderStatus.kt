package io.moya.shirtscanner.services.providers.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
enum class ProviderStatus {
    UP,
    DOWN,
    UNKNOWN,
}
