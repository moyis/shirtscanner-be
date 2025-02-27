package dev.moyis.shirtscanner.infrastructure.services

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.infrastructure.configuration.properties.DocumentFetcherConfigurationProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.util.ResourceUtils
import java.net.URI
import java.time.Duration

@WireMockTest
class ListR1ProductProviderTest {
    private val documentFetcher = DocumentFetcher(DocumentFetcherConfigurationProperties(Duration.ofMillis(500)))
    private val name = ProviderName("ListR1")
    private lateinit var subject: ListR1ProductProvider
    private lateinit var urlBase: URI
    private lateinit var wireMock: WireMock

    @BeforeEach
    fun setUp(wmRuntimeInfo: WireMockRuntimeInfo) {
        urlBase = URI(wmRuntimeInfo.httpBaseUrl)
        subject = ListR1ProductProvider(urlBase, name, documentFetcher)
        wireMock = wmRuntimeInfo.wireMock
    }

    @Test
    fun `a search on kitsgg returns products`() {
        val q = "france"
        setUpOkResponseForQuery(q, "kitsgg")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2022/23 France 1:1 Quality Away White Fans Soccer Jersey") },
            { assertThat(it.price).isEqualTo("US$ 14.58") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/2022-23-France-1-1-Quality-Away-White-Fans-Soccer-Jersey-p1654214.html") },
            { assertThat(it.imageLink.host).isEqualTo("us02-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/31229/2022/07/31/3/e/3e595d09fefc0c55.jpg") },
        )
        wireMock.findNearMissesForAllUnmatchedRequests().forEach { println(it.request.url) }
    }

    @Test
    fun `a search on 5boundless returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "5boundless")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2022 Argentina Away socks") },
            { assertThat(it.price).isEqualTo("US$ 5.00") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/2022-Argentina-Away-socks-p3449446.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/60655/2022/09/29/c/f/cfbb65c760300f31.jpg") },
        )
    }

    @Test
    fun `a search on grkits returns products`() {
        val q = "brazil"
        setUpOkResponseForQuery(q, "grkits")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2024 Brazil Blue Concept Edition Player Version Soccer Jersey") },
            { assertThat(it.price).isEqualTo("US$ 20.00") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/2024-Brazil-Blue-Concept-Edition-Player-Version-Soccer-Jersey-p1467478.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/97006/2023/12/24/4/3/43074b6d1a448047.jpg") },
        )
    }

    @Test
    fun `a search on gkkoc returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "gkkoc")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2023 Argentina Blue Special Edition Player Version Soccer Jersey") },
            { assertThat(it.price).isEqualTo("US$ 16.00") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/2023-Argentina-Blue-Special-Edition-Player-Version-Soccer-Jersey-p22397884.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/95755/2023/06/22/c/c/ccca7cd379f8be53.png") },
        )
    }

    @Test
    fun `a search on jeofc1 returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "jeofc1")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("2023 Argentina Blue Special Edition Player Version Soccer Jersey") },
            { assertThat(it.price).isEqualTo("US$ 16.00") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/2023-Argentina-Blue-Special-Edition-Player-Version-Soccer-Jersey-p22397840.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/81221/2023/06/22/f/6/f6fa467e08d6edd2.png") },
        )
    }

    @Test
    fun `a search on jjsport returns products`() {
        val q = "brazil"
        setUpOkResponseForQuery(q, "jjsport")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("Argentina 2024 Copa America Home Man Jersey") },
            { assertThat(it.price).isEqualTo("US$ 15.12") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/Argentina-2024-Copa-America-Home-Man-Jersey-p25179475.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/70745/2024/01/05/6/7/67a726ab5558f392.jpg") },
        )
    }

    @Test
    fun `a search on kegaoo returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "kegaoo")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("06-07 Argentina Away Retro Jersey Thailand Quality") },
            { assertThat(it.price).isEqualTo("US$ 19.98") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/06-07-Argentina-Away-Retro-Jersey-Thailand-Quality-p1048681.html") },
            { assertThat(it.imageLink.host).isEqualTo("us01-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/26617/2023/12/28/a/2/a24790c7601ceeba.jpg") },
        )
    }

    @Test
    fun `a search on kotofanss returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "kotofanss")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("Argentina Home Fans  1:1  24-25") },
            { assertThat(it.price).isEqualTo("US$ 13.00") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/Argentina-Home-Fans-1-1-24-25-p2445891.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/45427/2023/12/25/f/0/f0b1beda0b7f3f2f.jpg") },
        )
    }

    @Test
    fun `a search on soccer03 returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "soccer03")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("Argentina 2024 UEFA Champions League Home Man Jersey") },
            { assertThat(it.price).isEqualTo("US$ 14.61") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/Argentina-2024-UEFA-Champions-League-Home-Man-Jersey-p621694.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/43872/2024/01/05/a/5/a5e145887b0817d4.jpg") },
        )
    }

    @Test
    fun `a search on fofoshop returns products`() {
        val q = "argentina"
        setUpOkResponseForQuery(q, "fofoshop")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("22/23 Argentina Away World Cup Jersey Fans Version 1:1 Quality") },
            { assertThat(it.price).isEqualTo("US$ 15.90") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/22-23-Argentina-Away-World-Cup-Jersey-Fans-Version-1-1-Quality-p2092229.html") },
            { assertThat(it.imageLink.host).isEqualTo("us03-imgcdn.ymcart.com") },
            { assertThat(it.imageLink.path).isEqualTo("/51459/2022/09/05/f/f/ff56230aa700a900.jpg") },
        )
    }

    @Test
    fun `a search on aclotzone returns products`() {
        val q = "croatia"
        setUpOkResponseForQuery(q, "aclotzone")
        val result = subject.search(q)
        assertThat(result.products).singleElement().satisfies(
            { assertThat(it.name).isEqualTo("Croatia 2022 Home Soccer Jersey") },
            { assertThat(it.price).isEqualTo("US$ 18.99") },
            { assertThat(it.productLink.host).isEqualTo(urlBase.host) },
            { assertThat(it.productLink.path).isEqualTo("/Croatia-2022-Home-Soccer-Jersey-p1930861.html") },
            { assertThat(it.imageLink.host).isEqualTo("cdn.staticsab.com") },
            { assertThat(it.imageLink.path).isEqualTo("/50294/2022/03/28/7/2/721aef82ad2840af.png") },
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
        val body = provider?.let { ResourceUtils.getFile("classpath:providers/list-r1/$provider.html").readText() }
        wireMock.register(get(searchQuery(q)).willReturn(ok().withFixedDelay(duration.toMillis().toInt()).withBody(body)).withHeader())
    }

    private fun setUp4xxResponseForQuery(q: String) {
        wireMock.register(get(searchQuery(q)).willReturn(badRequest().withBody("")))
    }

    private fun setUp5xxResponseForQuery(q: String) {
        wireMock.register(get(searchQuery(q)).willReturn(serverError().withBody("")))
    }

    private fun searchQuery(q: String) = "/Search-$q/list--1000-1-2-----r1.html"
}
