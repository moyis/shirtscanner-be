package io.moya.shirtscanner.services.fetchers

import io.moya.shirtscanner.configuration.FetchersDefaultConfiguration
import io.moya.shirtscanner.models.Product
import io.moya.shirtscanner.models.SearchResult
import mu.KotlinLogging
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import java.net.SocketTimeoutException
import kotlin.time.measureTimedValue

private val LOG = KotlinLogging.logger { }

@Service
class YupooFetcher(
    val configuration: FetchersDefaultConfiguration
) : ProductsFetcher{
    override fun search(q: String, url: String): SearchResult {
        val products = getProducts(q,url)
        return SearchResult(
            queryUrl = getQueryUrl(q,url),
            products = products
        )
    }

    private fun getProducts(q: String, url: String): List<Product> {
        val query = getQueryUrl(q, url)
        val document = runCatching { fetchDocument(query) }.getOrElse { handleException(it, query) } ?: return listOf()
        val elementsByClass = document.getElementsByClass("album__main")
        return elementsByClass
            .asSequence()
            .mapNotNull { mapToProduct(it, url) }
            .toList()
    }

    private fun mapToProduct(element: Element, url: String): Product? {
        val name = element.attr("title") ?: return null
        val productLink = element.attr("href") ?: return null
        val imageLink = element.getElementsByClass("album__img").first()?.attr("data-src") ?: return null
        val product = Product(
            price = null,
            name = name,
            imageLink = "https:$imageLink",
            productLink = "$url$productLink"
        )
        return product
    }

    private fun fetchDocument(query: String): Document {
        LOG.debug { "Fetching products from $query" }
        val (document, durationFetch) = measureTimedValue {
            Jsoup.connect(query)
                .timeout(configuration.defaultTimeout.toMillis().toInt())
                .headers(DEFAULT_HEADERS)
                .get()
        }
        LOG.debug { "Fetching products from $query done. Took ${durationFetch.inWholeMilliseconds} ms" }
        return document
    }

    private fun handleException(throwable: Throwable, url: String): Document? {
        val baseMessage = "Exception found performing get on $url:"
        when (throwable) {
            is SocketTimeoutException -> LOG.warn { "$baseMessage took more than ${configuration.defaultTimeout.toMillis()}" }
            is HttpStatusException -> LOG.warn { "$baseMessage returned status code ${throwable.statusCode}" }
            is UnsupportedMimeTypeException -> {
                LOG.warn { "$baseMessage returned unsupported mime type ${throwable.mimeType}" }
            }

            else -> {
                LOG.error { "Unexpected exception found while performing get on $url: ${throwable.cause}" }
            }
        }
        return null
    }

    private fun getQueryUrl(q: String, url: String) = "$url/search/album?q=$q&uid=1&sort="

    companion object {
        private val DEFAULT_HEADERS = mapOf(
            HttpHeaders.USER_AGENT to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        )
    }
}
