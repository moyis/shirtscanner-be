package dev.moyis.shirtscanner.services.cache

import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration

private val LOG = KotlinLogging.logger { }

@Service
class RedissonCacheService(
    private val redissonClient: RedissonClient,
) : CacheService {
    override fun <T> computeIfAbsent(
        key: String,
        remappingFunction: () -> T,
    ): T {
        val bucket = redissonClient.getBucket<T?>(key)
        return runCatching { bucket.get() }
            .onFailure { LOG.error { it.message } }
            .getOrNull()
            ?: remappingFunction.invoke().also { bucket.set(it, Duration.ofDays(1)) }
    }

    override fun <T> set(
        key: String,
        value: T,
    ) {
        val bucket = redissonClient.getBucket<T>(key)
        bucket.set(value)
    }

    override fun <T> getAll(vararg keys: String): MutableMap<String, T> = redissonClient.buckets.get(*keys)
}
