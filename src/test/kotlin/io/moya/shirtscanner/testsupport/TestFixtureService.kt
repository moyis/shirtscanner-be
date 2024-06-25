package io.moya.shirtscanner.testsupport

import io.moya.shirtscanner.services.providers.model.ProviderStatus
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestFixtureService {
    @Autowired
    private lateinit var redissonClient: RedissonClient

    fun clearAll() {
        redissonClient.keys.flushdb()
    }

    fun persistStatus(
        providerName: String,
        status: ProviderStatus,
    ) {
        redissonClient.getBucket<ProviderStatus>("status_${providerName.replace(" ", "_")}")
            .set(status)
    }
}
