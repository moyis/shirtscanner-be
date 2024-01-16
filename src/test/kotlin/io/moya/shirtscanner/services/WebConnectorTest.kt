package io.moya.shirtscanner.services

import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.reset
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.moya.shirtscanner.configuration.WebConnectorConfigurationProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.retry.policy.NeverRetryPolicy
import org.springframework.retry.support.RetryTemplate
import java.time.Duration

@WireMockTest
class WebConnectorTest {
    private val defaultTimeout = Duration.ofMillis(300)

    private lateinit var subject: WebConnector

    @BeforeEach
    fun setUp() {
        reset()
        val configuration = WebConnectorConfigurationProperties(defaultTimeout)
        val retryTemplate = RetryTemplate.builder().customPolicy(NeverRetryPolicy()).build()
        subject = WebConnector(configuration, retryTemplate)
    }

    @Test
    fun `connector returns null when website returns 4xx`() {
        val path = "/my-path"
        setUp4xxResponseForQuery(path)
        val document = subject.fetchDocument(path)
        assertThat(document).isNull()
    }

    @Test
    fun `connector returns null when website returns 5xx`() {
        val path = "/my-path"
        setUp5xxResponseForQuery(path)
        val document = subject.fetchDocument(path)
        assertThat(document).isNull()
    }

    @Test
    fun `connector returns null when website takes too long to response`() {
        val path = "/my-path"
        setUpOkResponseForQuery(path, duration = Duration.ofSeconds(15))
        val document = subject.fetchDocument(path)
        assertThat(document).isNull()
    }

    @Test
    fun `a search should not take more than default timeout`() {
        val path = "/my-path"
        setUpOkResponseForQuery(path, duration = Duration.ofSeconds(15))
        assertTimeoutPreemptively(defaultTimeout) { subject.fetchDocument(path) }
    }

    private fun setUpOkResponseForQuery(
        path: String,
        duration: Duration = Duration.ZERO,
    ) {
        stubFor(get(path).willReturn(ok().withFixedDelay(duration.toMillis().toInt()).withBody("")))
    }

    private fun setUp4xxResponseForQuery(path: String) {
        stubFor(get(path).willReturn(badRequest().withBody("")))
    }

    private fun setUp5xxResponseForQuery(path: String) {
        stubFor(get(path).willReturn(serverError().withBody("")))
    }
}
