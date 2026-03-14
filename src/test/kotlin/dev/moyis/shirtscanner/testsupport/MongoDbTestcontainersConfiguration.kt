package dev.moyis.shirtscanner.testsupport

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.mongodb.MongoDBContainer


@TestConfiguration(proxyBeanMethods = false)
class MongoDbTestcontainersConfiguration {
    @Bean
    @ServiceConnection
    fun mongoContainer() = MongoDBContainer("mongo:8.0.6-noble")
}
