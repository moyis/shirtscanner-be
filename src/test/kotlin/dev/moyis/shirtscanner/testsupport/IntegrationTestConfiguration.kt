package dev.moyis.shirtscanner.testsupport

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.lifecycle.Startables
import org.wiremock.integrations.testcontainers.WireMockContainer

@TestConfiguration(proxyBeanMethods = false)
class IntegrationTestConfiguration : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private fun wiremockContainer(): WireMockContainer =
        WireMockContainer("wiremock/wiremock:3.11.0")
            .withMappingFromResource("wiremock/list-r-1.json")
            .withMappingFromResource("wiremock/yupoo.json")
            .withMappingFromResource("wiremock/yupoo-image.json")

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val wireMockContainer = wiremockContainer()

        Startables.deepStart(wireMockContainer).join()

        val properties =
            arrayOf(
                "wiremock.url=${wireMockContainer.baseUrl}",
            )
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, *properties)
    }
}
