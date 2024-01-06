package io.moya.shirtscanner.services.cache

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.Executors

@Service
class RedissonCacheService(
    private val redissonClient: RedissonClient,
) : CacheService {

    private val executorService = Executors.newVirtualThreadPerTaskExecutor()

    override fun <T> computeIfAbsent(key: String, remappingFunction: (String) -> T) : T {
        val bucket = redissonClient.getBucket<T?>(key)
        return bucket.get() ?: remappingFunction.invoke(key)
            .also { executorService.execute { bucket.set(it, Duration.ofHours(6)) } }
    }
}