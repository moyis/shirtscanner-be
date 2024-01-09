package io.moya.shirtscanner.services.cache

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.Executors

@Service
class RedissonCacheService(
    private val redissonClient: RedissonClient,
) : CacheService {
    override fun <T> computeIfAbsent(key: String, remappingFunction: () -> T): T {
        val bucket = redissonClient.getBucket<T?>(key)
        return bucket.get() ?: remappingFunction.invoke()
            .also { bucket.set(it, Duration.ofHours(6)) }
    }
}