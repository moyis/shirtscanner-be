package dev.moyis.shirtscanner.testsupport

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistrar
import org.wiremock.integrations.testcontainers.WireMockContainer

@TestConfiguration(proxyBeanMethods = false)
class RedisTestcontainersConfiguration {
    @Bean
    fun redis(): RedisContainer = RedisContainer("redis:7.4.2")

    @Bean
    fun redisConfiguration(container: RedisContainer) =
        DynamicPropertyRegistrar {
            it.add("redis.address") { container.redisURI }
        }
}

@TestConfiguration(proxyBeanMethods = false)
class WiremockTestcontainersConfiguration {
    @Bean
    fun wiremockContainer(): WireMockContainer =
        WireMockContainer("wiremock/wiremock:3.12.1")
            .withMappingFromResource("wiremock/list-r-1.json")
            .withMappingFromResource("wiremock/yupoo.json")
            .withMappingFromResource("wiremock/yupoo-image.json")

    @Bean
    fun wiremockConfiguration(container: WireMockContainer) =
        DynamicPropertyRegistrar {
            it.add("wiremock.url") { container.baseUrl }
        }
}
