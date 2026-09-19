package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.infrastructure.configuration.properties.CurlFetcherConfigurationProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.net.URI
import java.time.Duration

class CurlImpersonatingFetcherTest {

    private val runner = mock<CurlProcessRunner>()
    private val configuration =
        CurlFetcherConfigurationProperties(
            binary = "/usr/local/bin/curl_chrome120",
            defaultTimeout = Duration.ofSeconds(20),
            attempts = 5,
            backoff = Duration.ofMillis(1),
        )
    private val subject = CurlImpersonatingFetcher(configuration, runner)

    private val html =
        """
        <html><body><ul>
          <li><a href="/p1" title="Jersey A"><img src="/img1.png"></a><div class="price">US$ 10</div></li>
        </ul></body></html>
        """.trimIndent()

    @Test
    fun `parses a successful 200 response`() {
        whenever(runner.run(any(), any(), any())).thenReturn(CurlProcessRunner.Result(0, 200, html))

        val document = subject.fetchDocument(URI("https://www.example.com"))

        assertThat(document).isNotNull
        assertThat(document!!.select("li")).hasSize(1)
    }

    @Test
    fun `retries after a 403 challenge and succeeds`() {
        whenever(runner.run(any(), any(), any()))
            .thenReturn(CurlProcessRunner.Result(0, 403, "Just a moment..."))
            .thenReturn(CurlProcessRunner.Result(0, 403, "Just a moment..."))
            .thenReturn(CurlProcessRunner.Result(0, 200, html))

        val document = subject.fetchDocument(URI("https://www.example.com"))

        assertThat(document).isNotNull
        verify(runner, times(3)).run(any(), any(), any())
    }

    @Test
    fun `returns null when every attempt is challenged`() {
        whenever(runner.run(any(), any(), any()))
            .thenReturn(CurlProcessRunner.Result(0, 403, "Just a moment..."))

        val document = subject.fetchDocument(URI("https://www.example.com"))

        assertThat(document).isNull()
        verify(runner, times(configuration.attempts)).run(any(), any(), any())
    }

    @Test
    fun `returns null immediately on 404`() {
        whenever(runner.run(any(), any(), any()))
            .thenReturn(CurlProcessRunner.Result(0, 404, "not found"))

        assertThat(subject.fetchDocument(URI("https://www.example.com"))).isNull()
        verify(runner, times(1)).run(any(), any(), any())
    }

    @Test
    fun `returns null immediately on timeout`() {
        whenever(runner.run(any(), any(), any())).thenReturn(CurlProcessRunner.Result(28, null, null))

        assertThat(subject.fetchDocument(URI("https://www.example.com"))).isNull()
        verify(runner, times(1)).run(any(), any(), any())
    }

    @Test
    fun `retries when a challenge marker appears in a 200 body`() {
        whenever(runner.run(any(), any(), any()))
            .thenReturn(CurlProcessRunner.Result(0, 200, "Just a moment, verifying your browser..."))
            .thenReturn(CurlProcessRunner.Result(0, 200, html))

        assertThat(subject.fetchDocument(URI("https://www.example.com"))).isNotNull
        verify(runner, times(2)).run(any(), any(), any())
    }

    @Test
    fun `builds the curl-impersonate command`() {
        whenever(runner.run(any(), any(), any())).thenReturn(CurlProcessRunner.Result(0, 200, html))

        subject.fetchDocument(URI("https://www.example.com"))

        val captor = argumentCaptor<List<String>>()
        verify(runner).run(captor.capture(), any(), any())
        assertThat(captor.firstValue).contains("--http1.1", "--compressed", "--max-time", "20")
        assertThat(captor.firstValue).contains(
            "-A",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        )
        assertThat(captor.firstValue.first()).isEqualTo("/usr/local/bin/curl_chrome120")
    }
}