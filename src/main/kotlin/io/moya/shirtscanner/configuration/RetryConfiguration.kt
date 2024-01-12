package io.moya.shirtscanner.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

private const val MAX_RETRY_ATTEPMTS = 2

@Configuration
class RetryConfiguration {
    @Bean
    fun retryTemplate() =
        RetryTemplate().apply {
            setRetryPolicy(SimpleRetryPolicy(MAX_RETRY_ATTEPMTS))
        }
}
