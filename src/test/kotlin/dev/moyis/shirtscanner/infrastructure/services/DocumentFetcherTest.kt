package dev.moyis.shirtscanner.infrastructure.services

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.moyis.shirtscanner.infrastructure.configuration.properties.DocumentFetcherConfigurationProperties
import org.apache.http.HttpStatus
import org.apache.http.HttpStatus.SC_BAD_REQUEST
import org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import org.apache.http.HttpStatus.SC_TOO_MANY_REQUESTS
import org.apache.http.HttpStatus.SC_UNAUTHORIZED
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.util.ResourceUtils
import java.net.URI
import java.time.Duration

@WireMockTest
@ExtendWith(OutputCaptureExtension::class)
class DocumentFetcherTest {
    private lateinit var wiremockUrl: String
    private val properties = DocumentFetcherConfigurationProperties(Duration.ofSeconds(500))

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        WireMock.resetToDefault()
        wiremockUrl = wmRuntimeInfo.httpBaseUrl
    }

    @Test
    fun `fetches a document on successful request`() {
        setUpResponse(path = "/search")
        val fetcher = DocumentFetcher(properties)

        val document = fetcher.fetchDocument(URI("$wiremockUrl/search"))

        Assertions.assertThat(document).isNotNull
    }

    @Test
    fun `returns null on timeout`() {
        setUpResponse(path = "/search", delay = Duration.ofMinutes(1))
        val fetcher = DocumentFetcher(properties.copy(defaultTimeout = Duration.ofMillis(100)))

        val document = fetcher.fetchDocument(URI("$wiremockUrl/search"))

        Assertions.assertThat(document).isNull()
    }

    @Test
    fun `logs default timeout on timeout`(output: CapturedOutput) {
        setUpResponse(path = "/search", delay = Duration.ofMinutes(1))
        val fetcher = DocumentFetcher(properties.copy(defaultTimeout = Duration.ofMillis(100)))

        fetcher.fetchDocument(URI("$wiremockUrl/search"))

        Assertions.assertThat(output).contains("took more than 100")
    }

    @ParameterizedTest
    @ValueSource(ints = [SC_BAD_REQUEST, SC_UNAUTHORIZED, SC_TOO_MANY_REQUESTS, SC_INTERNAL_SERVER_ERROR])
    fun `logs default timeout on timeout`(
        status: Int,
        output: CapturedOutput,
    ) {
        setUpResponse(path = "/search", status = status)
        val fetcher = DocumentFetcher(properties)

        fetcher.fetchDocument(URI("$wiremockUrl/search"))

        Assertions.assertThat(output).contains("returned status code $status HTTP error fetching URL")
    }

    private fun setUpResponse(
        path: String,
        delay: Duration = Duration.ZERO,
        status: Int = HttpStatus.SC_OK,
    ) {
        val body = ResourceUtils.getFile("classpath:providers/list-r1/5boundless.html").readText()
        WireMock.stubFor(
            WireMock.get(path)
                .willReturn(WireMock.status(status).withFixedDelay(delay.toMillis().toInt()).withBody(body)),
        )
    }
}
