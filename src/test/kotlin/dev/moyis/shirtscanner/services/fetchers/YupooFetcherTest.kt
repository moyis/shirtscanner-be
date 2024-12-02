package dev.moyis.shirtscanner.services.fetchers

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.moyis.shirtscanner.configuration.WebConnectorConfigurationProperties
import dev.moyis.shirtscanner.configuration.YupooFetcherConfigurationProperties
import dev.moyis.shirtscanner.services.WebConnector
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.retry.policy.NeverRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.util.ResourceUtils
import java.time.Duration

@WireMockTest
class YupooFetcherTest {
    private lateinit var subject: YupooFetcher
    private lateinit var urlBase: String

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        WireMock.resetToDefault()
        urlBase = wmRuntimeInfo.httpBaseUrl
        val configuration = WebConnectorConfigurationProperties(Duration.ofMillis(500))
        val retryTemplate = RetryTemplate.builder().customPolicy(NeverRetryPolicy()).build()
        val webConnector = WebConnector(configuration, retryTemplate)
        subject = YupooFetcher(webConnector, YupooFetcherConfigurationProperties(urlBase))
    }

    @Test
    fun `a search on yupoo returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "beonestore")
        val result = subject.search(q, urlBase)
        assertThat(result.products).hasSize(38)
        assertThat(result.products).first().satisfies(
            { assertThat(it.name).isEqualTo("Retro 2000 Argentina home") },
            { assertThat(it.productLink).isEqualTo("$urlBase/albums/90802644?uid=1") },
            { assertThat(it.price).isNull() },
            { assertThat(it.imageLink).isEqualTo("$urlBase/v1/images/yupoo?path=beonestore/116b7fcf/medium.jpg") },
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

    private fun setUpOkResponseForQuery(
        q: String,
        provider: String? = null,
        duration: Duration = Duration.ZERO,
    ) {
        val body = if (provider != null) ResourceUtils.getFile("classpath:providers/yupoo/$provider.html").readText() else ""
        stubFor(get(searchQuery(q)).willReturn(ok().withFixedDelay(duration.toMillis().toInt()).withBody(body)))
    }

    private fun setUp4xxResponseForQuery(q: String) {
        stubFor(get(searchQuery(q)).willReturn(badRequest().withBody("")))
    }

    private fun setUp5xxResponseForQuery(q: String) {
        stubFor(get(searchQuery(q)).willReturn(serverError().withBody("")))
    }

    private fun searchQuery(q: String) = "/search/album?q=$q&uid=1&sort=&page=1"
}
