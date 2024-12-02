package dev.moyis.shirtscanner.testsupport

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.lifecycle.Startables
import org.wiremock.integrations.testcontainers.WireMockContainer

@TestConfiguration(proxyBeanMethods = false)
class IntegrationTestConfiguration : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private fun redisContainer(): RedisContainer = RedisContainer("redis:6.2.6-alpine")

    private fun wiremockContainer(): WireMockContainer =
        WireMockContainer("wiremock/wiremock:3.3.1-1-alpine")
            .withMappingFromResource("wiremock/list-r-1.json")
            .withMappingFromResource("wiremock/yupoo.json")

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val redisContainer = redisContainer()
        val wireMockContainer = wiremockContainer()

        Startables.deepStart(redisContainer, wireMockContainer).join()

        val properties =
            arrayOf(
                "wiremock.url=${wireMockContainer.baseUrl}",
                "redis.host=${redisContainer.redisURI}",
            )
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, *properties)
    }
}
