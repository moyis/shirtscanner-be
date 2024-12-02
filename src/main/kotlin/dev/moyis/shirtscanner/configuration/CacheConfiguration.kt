package dev.moyis.shirtscanner.configuration

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.config.SingleServerConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfiguration(
    private val configurationProperties: RedisConfigurationProperties,
) {
    @Bean
    fun redisson(): RedissonClient {
        val config =
            with(configurationProperties) {
                Config().apply {
                    useSingleServer().apply {
                        SingleServerConfig.setAddress = host
                    }
                }
            }

        return Redisson.create(config)
    }
}

@ConfigurationProperties(prefix = "redis")
data class RedisConfigurationProperties(
    val host: String,
)
