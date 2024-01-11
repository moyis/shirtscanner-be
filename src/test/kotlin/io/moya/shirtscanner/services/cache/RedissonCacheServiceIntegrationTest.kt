package io.moya.shirtscanner.services.cache

import io.moya.shirtscanner.testsupport.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

private const val CACHE_KEY = "my_key"

class RedissonCacheServiceIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var subject: RedissonCacheService

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @Nested
    inner class CacheEmpty {
        @Test
        fun `value is saved in cache`() {
            val expectedValue = "My Value"
            val result = subject.computeIfAbsent(CACHE_KEY) { expectedValue }
            assertThat(result).isEqualTo(expectedValue)
            await().atMost(Duration.ofSeconds(1))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted { assertThat(redissonClient.getBucket<String?>(CACHE_KEY).get()).isEqualTo(expectedValue) }

        }
    }

    @Nested
    inner class HasAValue {

        private val persistedValue = "My Old Value"

        @BeforeEach
        fun setUp() {
            redissonClient.getBucket<String?>(CACHE_KEY).set(persistedValue)
        }

        @Test
        fun `provider function is not called`() {
            val result = subject.computeIfAbsent(CACHE_KEY) { "My New Value" }
            assertThat(result).isEqualTo(persistedValue)
        }
    }
}