package io.moya.shirtscanner.services.fetchers

import io.moya.shirtscanner.configuration.YupooFetcherConfigurationProperties
import io.moya.shirtscanner.models.Product
import io.moya.shirtscanner.models.SearchResult
import io.moya.shirtscanner.services.WebConnector
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class YupooFetcher(
    private val webConnector: WebConnector,
    private val configuration: YupooFetcherConfigurationProperties,
) : ProductsFetcher {
    override fun search(
        q: String,
        url: String,
    ): SearchResult {
        val products = getProducts(q, url)
        return SearchResult(
            queryUrl = getWebpageUrl(q, url, 1),
            products = products,
        )
    }

    private fun getProducts(
        q: String,
        url: String,
    ) = getDocuments(q, url)
        .flatMap { it.getElementsByClass("album__main") }
        .mapNotNull { mapToProduct(it, url) }
        .toList()

    private fun getDocuments(
        q: String,
        url: String,
    ) = generateSequence(1) { it + 1 }
        .map { getWebpageUrl(q, url, it) }
        .take(2)
        .mapNotNull { webConnector.fetchDocument(it) }
        .takeWhile { it.getElementsByClass("empty__emptytitle").isEmpty() }

    private fun mapToProduct(
        element: Element,
        url: String,
    ): Product? {
        val name = element.attr("title") ?: return null
        val productLink = element.attr("href") ?: return null
        val imageLink =
            element.getElementsByClass("album__img").first()
                ?.attr("data-src")
                ?.substringAfter("photo.yupoo.com/")
                ?: return null
        val product =
            Product(
                price = null,
                name = name,
                imageLink = "${configuration.imageProxyHost}/v1/images/yupoo?path=$imageLink",
                productLink = "$url$productLink",
            )
        return product
    }

    private fun getWebpageUrl(
        q: String,
        url: String,
        pageNumber: Int,
    ) = "$url/search/album?q=$q&uid=1&sort=&page=$pageNumber"
}
