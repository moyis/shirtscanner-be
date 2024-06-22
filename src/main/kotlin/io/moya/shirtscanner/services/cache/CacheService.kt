package io.moya.shirtscanner.services.cache

interface CacheService {
    fun <T> computeIfAbsent(
        key: String,
        remappingFunction: () -> T,
    ): T

    fun <T> set(
        key: String,
        value: T,
    )

    fun <T> getAll(vararg keys: String): MutableMap<String, T>
}
