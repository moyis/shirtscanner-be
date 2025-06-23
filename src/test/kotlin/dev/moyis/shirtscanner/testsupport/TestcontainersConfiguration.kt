package dev.moyis.shirtscanner.testsupport

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistrar
import org.testcontainers.containers.MongoDBContainer
import org.wiremock.integrations.testcontainers.WireMockContainer

@TestConfiguration(proxyBeanMethods = false)
class MongoDbTestcontainersConfiguration {
    @Bean
    @ServiceConnection
    fun mongoContainer() = MongoDBContainer("mongo:8.0.6-noble")
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
