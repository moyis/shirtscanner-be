package io.moya.shirtscanner.services.cache

interface CacheService {
    fun <T> getAndSetIfAbsent(key: String, provider: () -> T) : T
}