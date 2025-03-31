package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.CacheConfigurationProperties
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfiguration(
    private val properties: CacheConfigurationProperties,
) {
    @Bean
    fun redisson(): RedissonClient {
        val config =
            Config().apply {
                useSingleServer().apply {
                    address = properties.address
                }
            }

        return Redisson.create(config)
    }
}
