package io.moya.shirtscanner.services.cache

interface CacheService {
    fun <T> computeIfAbsent(
        key: String,
        remappingFunction: () -> T,
    ): T
}
