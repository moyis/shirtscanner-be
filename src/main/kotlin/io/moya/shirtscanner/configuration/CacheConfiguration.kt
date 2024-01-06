package io.moya.shirtscanner.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration {

    @Bean
    fun searchCache() = createCache(CACHE_SEARCH)

    private fun createCache(
        name: String,
        initialCapacity: Int = 20,
        maxCapacity: Long = 1000,
        expireAfterAccess: Duration = Duration.ofHours(6),
        expireAfterWrite: Duration = Duration.ofHours(12),
    ) = CaffeineCache(
        name,
        Caffeine.newBuilder()
            .initialCapacity(initialCapacity)
            .maximumSize(maxCapacity)
            .expireAfterAccess(expireAfterAccess)
            .expireAfterWrite(expireAfterWrite)
            .recordStats()
            .build(),
    )

    companion object {
        const val CACHE_SEARCH = "search"
    }
}