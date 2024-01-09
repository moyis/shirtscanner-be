package io.moya.shirtscanner.configuration

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.Kryo5Codec
import org.redisson.codec.SnappyCodecV2
import org.redisson.config.Config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfiguration(
    private val configurationProperties: RedisConfigurationProperties,
) {

    @Bean
    fun redisson(): RedissonClient {
        val config = with(configurationProperties) {
            Config().apply {
                useSingleServer().apply {
                    address = host
                    codec = SnappyCodecV2()
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