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

private const val i = 1

class RedissonCacheServiceIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var subject: RedissonCacheService

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @Nested
    inner class CacheEmpty {
        @Test
        fun `value is saved in cache`() {
            val expectedValue = listOf("My Value")
            val result = subject.computeIfAbsent(CACHE_KEY) { expectedValue }
            assertThat(result).isEqualTo(expectedValue)
            await().atMost(Duration.ofSeconds(1))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted { assertThat(redissonClient.getBucket<List<String>?>(CACHE_KEY).get()).isEqualTo(expectedValue) }

        }
    }

    @Nested
    inner class HasAValue {

        private val persistedValue = listOf("My Old Value")

        @BeforeEach
        fun setUp() {
            redissonClient.getBucket<List<String>?>(CACHE_KEY).set(persistedValue)
        }

        @Test
        fun `provider function is not called`() {
            val result = subject.computeIfAbsent(CACHE_KEY) { listOf("My New Value") }
            assertThat(result).isEqualTo(persistedValue)
        }
    }
}