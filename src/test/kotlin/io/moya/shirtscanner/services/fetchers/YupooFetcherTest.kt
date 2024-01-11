package io.moya.shirtscanner.services.fetchers

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.moya.shirtscanner.configuration.FetchersDefaultConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import org.springframework.util.ResourceUtils
import java.time.Duration

@WireMockTest
class YupooFetcherTest {

    private lateinit var subject: YupooFetcher
    private val defaultTimeout = Duration.ofMillis(500)
    private lateinit var urlBase: String

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        WireMock.resetToDefault()
        val configuration = FetchersDefaultConfiguration(defaultTimeout)
        urlBase = wmRuntimeInfo.httpBaseUrl
        subject = YupooFetcher(configuration)
    }

    @Test
    fun `a search on yupoo returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "beonestore")
        val result = subject.search(q, urlBase)
        assertThat(result.products).hasSize(38)
        assertThat(result.products).first().satisfies(
            { assertThat(it.name).isEqualTo("Retro 2000 Argentina home") },
            { assertThat(it.productLink).isEqualTo("${urlBase}/albums/90802644?uid=1") },
            { assertThat(it.price).isNull() },
            { assertThat(it.imageLink).isEqualTo("https://photo.yupoo.com/beonestore/116b7fcf/medium.jpg") },
        )
    }

    @Test
    fun `a search returns provider url`() {
        val q = "anything"
        setUpOkResponseForQuery(q)
        val result = subject.search(q, urlBase)
        assertThat(result.queryUrl).isEqualTo("${urlBase}${searchQuery(q)}")
    }

    @Test
    fun `a search returns empty products when website returns 4xx`() {
        val q = "anything"
        setUp4xxResponseForQuery(q)
        val result = subject.search(q, urlBase)
        assertThat(result.products).isEmpty()
    }

    @Test
    fun `a search returns empty products when website returns 5xx`() {
        val q = "anything"
        setUp5xxResponseForQuery(q)
        val result = subject.search(q, urlBase)
        assertThat(result.products).isEmpty()
    }

    @Test
    fun `a search that takes too long should return empty products`() {
        val q = "anything"
        setUpOkResponseForQuery(q, duration = Duration.ofSeconds(15))
        val result = subject.search(q, urlBase)
        assertThat(result.products).isEmpty()
    }

    @Test
    fun `a search should not take more than default timeout`() {
        val q = "anything"
        setUpOkResponseForQuery(q, duration = Duration.ofSeconds(15))
        assertTimeoutPreemptively(defaultTimeout) { subject.search(q, urlBase) }
    }

    private fun setUpOkResponseForQuery(q: String, provider: String? = null, duration: Duration = Duration.ZERO) {
        val body = if (provider != null) ResourceUtils.getFile("classpath:providers/yupoo/$provider.html").readText() else ""
        stubFor(get(searchQuery(q)).willReturn(ok().withFixedDelay(duration.toMillis().toInt()).withBody(body)))
    }

    private fun setUp4xxResponseForQuery(q: String) {
        stubFor(get(searchQuery(q)).willReturn(badRequest().withBody("")))
    }

    private fun setUp5xxResponseForQuery(q: String) {
        stubFor(get(searchQuery(q)).willReturn(serverError().withBody("")))
    }

    private fun searchQuery(q: String) = "/search/album?q=$q&uid=1&sort="
}
