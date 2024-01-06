package io.moya.shirtscanner.services.cache

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class RedissonCacheService(
    private val redissonClient: RedissonClient,
) : CacheService {

    private val executorService = Executors.newVirtualThreadPerTaskExecutor()

    override fun <T> getAndSetIfAbsent(key: String, provider: () -> T) : T {
        val bucket = redissonClient.getBucket<T?>(key)
        return bucket.get() ?: provider.invoke()
            .also { executorService.execute { bucket.set(it, Duration.ofHours(6)) } }
    }
}