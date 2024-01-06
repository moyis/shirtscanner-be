package io.moya.shirtscanner.testsupport

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class IntegrationTestConfiguration {

    @Bean
    fun redisContainer(registry: DynamicPropertyRegistry): RedisContainer {
        val redis = RedisContainer(DockerImageName.parse("redis:7.0.11-alpine"))
        with(registry) {
            add("redis.host") { redis.host }
            add("redis.port") { redis.firstMappedPort }
        }
        return redis
    }
}

