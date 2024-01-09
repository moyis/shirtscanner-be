package io.moya.shirtscanner.services.cache

import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.Executors

private val LOG = KotlinLogging.logger { }

@Service
class RedissonCacheService(
    private val redissonClient: RedissonClient,
) : CacheService {
    override fun <T> computeIfAbsent(key: String, remappingFunction: () -> T): T {
        val value = runCatching { redissonClient.getBucket<T?>(key).get() ?: remappingFunction.invoke() }
            .onFailure { LOG.error { "Exception found" } }
            .getOrElse { remappingFunction.invoke() }
        runCatching { redissonClient.getBucket<T?>(key).get() }
            .onFailure { LOG.error { "Exception found" } }
        return value
    }
}