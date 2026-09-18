package dev.moyis.shirtscanner.infrastructure.services

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import dev.moyis.shirtscanner.infrastructure.configuration.properties.ImageFetcherConfigurationProperties
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders.REFERER
import reactor.core.publisher.Flux
import java.net.URI
import java.time.Duration

@WireMockTest
class WebClientImageProviderTest {
    private lateinit var subject: WebClientImageProvider

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        WireMock.reset()
        val config =
            ImageFetcherConfigurationProperties(
                baseUrl = URI(wmRuntimeInfo.httpBaseUrl),
                referer = URI("http://localhost"),
                maxRetries = 3,
                retryDelay = Duration.ZERO,
            )
        subject = WebClientImageProvider(config)
    }

    @Test
    fun `returns image content as byte array`() =
        runTest {
            stubFor(
                get(urlEqualTo("/image.jpg"))
                    .withHeader(REFERER, equalTo("http://localhost"))
                    .willReturn(aResponse().withBody("image".toByteArray())),
            )

            val image = subject.get("image.jpg").awaitBytes()

            assertThat(image).isEqualTo("image".toByteArray())
        }

    @Test
    fun `completes empty if image is unavailable`() =
        runTest {
            stubFor(
                get(urlEqualTo("/not-found.jpg"))
                    .withHeader(REFERER, equalTo("http://localhost"))
                    .willReturn(aResponse().withStatus(404)),
            )

            val image = subject.get("/not-found.jpg").awaitBytes()

            assertThat(image).isNull()
        }

    @Test
    fun `retries 3 times if image is unavailable`() =
        runTest {
            setUpThreeRetryScenario("/too-many-requests.jpg", "image")

            val image = subject.get("/too-many-requests.jpg").awaitBytes()

            assertThat(image).isEqualTo("image".toByteArray())
        }

    private suspend fun Flux<DataBuffer>.awaitBytes(): ByteArray? {
        val joined = DataBufferUtils.join(this).awaitSingleOrNull() ?: return null
        return try {
            joined.readBytes()
        } finally {
            DataBufferUtils.release(joined)
        }
    }

    private fun DataBuffer.readBytes(): ByteArray {
        val bytes = ByteArray(readableByteCount())
        read(bytes)
        return bytes
    }

    private fun setUpThreeRetryScenario(
        path: String,
        content: String,
    ) {
        stubFor(
            get(urlEqualTo(path))
                .inScenario("retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("first-retry"),
        )

        stubFor(
            get(urlEqualTo(path))
                .inScenario("retry")
                .whenScenarioStateIs("first-retry")
                .willReturn(aResponse().withStatus(429))
                .willSetStateTo("second-retry"),
        )

        stubFor(
            get(urlEqualTo(path))
                .inScenario("retry")
                .whenScenarioStateIs("second-retry")
                .willReturn(aResponse().withBody(content)),
        )
    }
}
