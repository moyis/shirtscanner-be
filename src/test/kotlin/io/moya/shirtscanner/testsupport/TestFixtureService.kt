package io.moya.shirtscanner.testsupport

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
}