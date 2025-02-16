package dev.moyis.shirtscanner.infrastructure.services

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.infrastructure.configuration.properties.DocumentFetcherConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.configuration.properties.YupooProviderConfigurationProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.util.ResourceUtils
import java.net.URI
import java.time.Duration

@WireMockTest
class YupooProductProviderTest {
    private lateinit var subject: YupooProductProvider
    private lateinit var urlBase: URI
    private val documentFetcher = DocumentFetcher(DocumentFetcherConfigurationProperties(Duration.ofMillis(500)))

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        WireMock.resetToDefault()
        urlBase = URI(wmRuntimeInfo.httpBaseUrl)
        val yupooConfig = YupooProviderConfigurationProperties(urlBase)
        subject = YupooProductProvider(urlBase, ProviderName("Yupoo"), documentFetcher, yupooConfig)
    }

    @Test
    fun `a search on yupoo returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "beonestore")
        val result = subject.search(q)
        assertThat(result.products).hasSize(38)
        assertThat(result.products).first().satisfies(
            { assertThat(it.name).isEqualTo("Retro 2000 Argentina home") },
            { assertThat(it.productLink).isEqualTo(URI("$urlBase/albums/90802644?uid=1")) },
            { assertThat(it.price).isNull() },
            { assertThat(it.imageLink).isEqualTo(URI("$urlBase/v1/images/yupoo?path=beonestore/116b7fcf/medium.jpg")) },
        )
    }

    @Test
    fun `a search returns provider url`() {
        val q = "anything"
        setUpOkResponseForQuery(q)
        val result = subject.search(q)
        assertThat(result.queryUrl).isEqualTo(URI("${urlBase}${searchQuery(q)}"))
    }

    @Test
    fun `a search returns empty products when website returns 4xx`() {
        val q = "anything"
        setUp4xxResponseForQuery(q)
        val result = subject.search(q)
        assertThat(result.products).isEmpty()
    }

    @Test
    fun `a search returns empty products when website returns 5xx`() {
        val q = "anything"
        setUp5xxResponseForQuery(q)
        val result = subject.search(q)
        assertThat(result.products).isEmpty()
    }

    @Test
    fun `a search that takes too long should return empty products`() {
        val q = "anything"
        setUpOkResponseForQuery(q, duration = Duration.ofSeconds(15))
        val result = subject.search(q)
        assertThat(result.products).isEmpty()
    }

    private fun setUpOkResponseForQuery(
        q: String,
        provider: String? = null,
        duration: Duration = Duration.ZERO,
    ) {
        val body =
            if (provider != null) {
                ResourceUtils.getFile("classpath:providers/yupoo/$provider.html").readText()
            } else {
                ""
            }
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
