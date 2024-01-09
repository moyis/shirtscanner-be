package io.moya.shirtscanner.services.fetchers

import io.moya.shirtscanner.models.Product
import io.moya.shirtscanner.models.SearchResult
import mu.KotlinLogging
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.http.HttpHeaders
import java.net.SocketTimeoutException
import java.time.Duration
import kotlin.time.measureTimedValue

private val LOG = KotlinLogging.logger { }

class DefaultFetcher(
    private val url: String,
    private val defaultTimeout: Duration,
) : ProductsFetcher {
    override fun search(q: String): SearchResult {
        val query = """$url/Search-$q/list--1000-1-2-----r1.html"""
        val document = runCatching { fetchDocument(query) }.getOrElse { handleException(it) } ?: return searchResult(query)
        val products = document.select("li")
            .asSequence()
            .mapNotNull { mapToProduct(it) }
            .toList()
        return searchResult(query, products)
    }

    private fun searchResult(query: String, products: List<Product> = emptyList()) = SearchResult(
        queryUrl = query,
        products = products,
    )

    private fun fetchDocument(query: String): Document {
        LOG.debug { "Fetching products from $query" }
        val (document, durationFetch) = measureTimedValue {
            Jsoup.connect(query)
                .timeout(defaultTimeout.toMillis().toInt())
                .headers(DEFAULT_HEADERS)
                .get()
        }
        LOG.debug { "Fetching products from $query done. Took ${durationFetch.inWholeMilliseconds} ms" }
        return document
    }

    private fun mapToProduct(element: Element): Product? {
        val a = element.selectFirst("a") ?: return null
        val price = element.getElementsByClass("price").first() ?: return null
        val imageLink = element.selectFirst("img")?.attr("src") ?: return null
        val productLink = a.attr("href")
        val name = a.attr("title")
        return Product(
            name = name,
            price = price.text(),
            productLink = "$url$productLink",
            imageLink = imageLink
        )
    }

    private fun handleException(throwable: Throwable): Document? {
        val baseMessage = "Exception found performing get on $url:"
        when (throwable) {
            is SocketTimeoutException -> LOG.warn { "$baseMessage took more than ${defaultTimeout.toMillis()}"}
            is HttpStatusException -> LOG.warn { "$baseMessage returned status code ${throwable.statusCode}" }
            is UnsupportedMimeTypeException -> { LOG.warn { "$baseMessage returned unsupported mime type ${throwable.mimeType}" } }
            else -> { LOG.error { "Unexpected exception found while performing get on $url: ${throwable.cause}" } }
        }
        return null
    }

    companion object {
        private val DEFAULT_HEADERS = mapOf(
            "Dnt" to "1",
            "Sec-Ch-Ua" to """"Not_A Brand";v="8", "Chromium";v="120"""",
            "Sec-Ch-Ua-Mobile" to "?0",
            "Sec-Ch-Ua-Platform" to "\"macOS\"",
            "Sec-Fetch-Dest" to "document",
            "Sec-Fetch-Mode" to "navigate",
            "Sec-Fetch-Site" to "same-origin",
            "Sec-Fetch-User" to "?1",
            "Upgrade-Insecure-Requests" to "1",
            HttpHeaders.USER_AGENT to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        )
    }
}
