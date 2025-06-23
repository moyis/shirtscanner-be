package dev.moyis.shirtscanner.infrastructure.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class ServiceConfiguration {
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}
