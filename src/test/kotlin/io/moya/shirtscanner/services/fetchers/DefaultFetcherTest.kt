package io.moya.shirtscanner.services.fetchers

import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.util.ResourceUtils

@WireMockTest
class DefaultFetcherTest {

    private lateinit var subject: DefaultFetcher

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        subject = DefaultFetcher(wmRuntimeInfo.httpBaseUrl)
    }

    @Test
    fun `a search on kitsgg returns products`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "france"
        setUpOkResponseForQuery(q, "kitsgg")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2022/23 France 1:1 Quality Away White Fans Soccer Jersey") },
            { assertThat(it.productLink).isEqualTo("${wmRuntimeInfo.httpBaseUrl}/2022-23-France-1-1-Quality-Away-White-Fans-Soccer-Jersey-p1654214.html") },
            { assertThat(it.price).isEqualTo("US\$ 14.58") },
            { assertThat(it.imageLink).isEqualTo("https://us02-imgcdn.ymcart.com/31229/2022/07/31/3/e/3e595d09fefc0c55.jpg?x-oss-process=image/quality,Q_80/resize,m_lfit,w_220,h_220/interlace,0/format,webp") },
        )
    }

    @Test
    fun `a search on 5boundless returns products`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "argentina"
        setUpOkResponseForQuery(q, "5boundless")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2022 Argentina Away socks") },
            { assertThat(it.productLink).isEqualTo("${wmRuntimeInfo.httpBaseUrl}/2022-Argentina-Away-socks-p3449446.html") },
            { assertThat(it.price).isEqualTo("US\$ 5.00") },
            { assertThat(it.imageLink).isEqualTo("https://us03-imgcdn.ymcart.com/60655/2022/09/29/c/f/cfbb65c760300f31.jpg?x-oss-process=image/quality,Q_90/auto-orient,1/resize,m_lfit,w_500,h_500/format,webp") },
        )
    }

    @Test
    fun `a search on grkits returns products`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "brazil"
        setUpOkResponseForQuery(q, "grkits")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2024 Brazil Blue Concept Edition Player Version Soccer Jersey") },
            { assertThat(it.productLink).isEqualTo("${wmRuntimeInfo.httpBaseUrl}/2024-Brazil-Blue-Concept-Edition-Player-Version-Soccer-Jersey-p1467478.html") },
            { assertThat(it.price).isEqualTo("US\$ 20.00") },
            { assertThat(it.imageLink).isEqualTo("https://us03-imgcdn.ymcart.com/97006/2023/12/24/4/3/43074b6d1a448047.jpg?x-oss-process=image/quality,Q_90/auto-orient,1/resize,m_lfit,w_210,h_210") },
        )
    }

    @Test
    fun `a search on aclotzone returns products`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "croatia"
        setUpOkResponseForQuery(q, "aclotzone")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("Croatia 2022 Home Soccer Jersey") },
            { assertThat(it.productLink).isEqualTo("${wmRuntimeInfo.httpBaseUrl}/Croatia-2022-Home-Soccer-Jersey-p1930861.html") },
            { assertThat(it.price).isEqualTo("US\$ 18.99") },
            { assertThat(it.imageLink).isEqualTo("https://cdn.staticsab.com/50294/2022/03/28/7/2/721aef82ad2840af.png?x-oss-process=image/quality,Q_80/resize,m_lfit,w_500,h_500/interlace,0/auto-orient,0/format,jpg") },
        )
    }

    @Test
    fun `a search returns provider url`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "anything"
        setUpOkResponseForQuery(q)
        val result = subject.search(q)
        assertThat(result.queryUrl).isEqualTo("${wmRuntimeInfo.httpBaseUrl}${searchQuery(q)}")
    }

    @Test
    fun `a search returns empty products when website returns 4xx`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "anything"
        setUp4xxResponseForQuery(q)
        val result = subject.search(q)
        assertThat(result.products).isEmpty()
    }

    @Test
    fun `a search returns empty products when website returns 5xx`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val q = "anything"
        setUp5xxResponseForQuery(q)
        val result = subject.search(q)
        assertThat(result.products).isEmpty()
    }

    private fun setUpOkResponseForQuery(q: String, provider: String? = null) {
        val body = if (provider != null) ResourceUtils.getFile("classpath:providers/$provider.html").readText() else ""
        stubFor(get(searchQuery(q)).willReturn(ok().withBody(body)))
    }

    private fun setUp4xxResponseForQuery(q: String) {
        stubFor(get(searchQuery(q)).willReturn(badRequest().withBody("")))
    }

    private fun setUp5xxResponseForQuery(q: String) {
        stubFor(get(searchQuery(q)).willReturn(serverError().withBody("")))
    }

    private fun searchQuery(q: String) = "/Search-$q/list--1000-1-2-----r1.html"
}