package io.moya.shirtscanner.testsupport

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.wiremock.integrations.testcontainers.WireMockContainer

@TestConfiguration(proxyBeanMethods = false)
class IntegrationTestConfiguration {
    @Bean
    fun redisContainer(registry: DynamicPropertyRegistry): RedisContainer {
        val redis = RedisContainer("redis:6.2.6-alpine")
        with(registry) {
            add("redis.host") { "redis://${redis.host}:${redis.firstMappedPort}" }
        }
        return redis
    }

    @Bean
    fun wiremockContainer(registry: DynamicPropertyRegistry): WireMockContainer {
        val wiremock =
            WireMockContainer("wiremock/wiremock:3.3.1-1-alpine")
                .withMappingFromResource("wiremock/list-r-1.json")
                .withMappingFromResource("wiremock/yupoo.json")
        with(registry) {
            add("wiremock.url") { wiremock.baseUrl }
        }
        return wiremock
    }
}
