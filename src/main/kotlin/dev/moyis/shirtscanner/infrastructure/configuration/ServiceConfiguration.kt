package dev.moyis.shirtscanner.infrastructure.configuration

import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.util.concurrent.TimeUnit

@Configuration
class ServiceConfiguration {
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()

    @Bean
    fun mongoClientSettingsBuilderCustomizer(): MongoClientSettingsBuilderCustomizer =
        MongoClientSettingsBuilderCustomizer {
            it.applyToClusterSettings { cluster ->
                cluster.serverSelectionTimeout(2, TimeUnit.SECONDS)
            }
        }
}
