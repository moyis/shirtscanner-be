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
    private val configuration: YupooFetcherConfigurationProperties
) : ProductsFetcher {

    override fun search(q: String, url: String): SearchResult {
        val products = getProducts(q, url)
        return SearchResult(
            queryUrl = getWebpageUrl(q, url),
            products = products
        )
    }

    private fun getProducts(q: String, url: String): List<Product> {
        val webpage = getWebpageUrl(q, url)
        val document = webConnector.fetchDocument(webpage) ?: return listOf()
        val elementsByClass = document.getElementsByClass("album__main")
        return elementsByClass
            .asSequence()
            .mapNotNull { mapToProduct(it, url) }
            .toList()
    }

    private fun mapToProduct(element: Element, url: String): Product? {
        val name = element.attr("title") ?: return null
        val productLink = element.attr("href") ?: return null
        val imageLink = element.getElementsByClass("album__img").first()
            ?.attr("data-src")
            ?.substringAfter("photo.yupoo.com/")
            ?: return null
        val product = Product(
            price = null,
            name = name,
            imageLink = "${configuration.imageProxyHost}/v1/images/yupoo?path=$imageLink",
            productLink = "$url$productLink"
        )
        return product
    }

    private fun getWebpageUrl(q: String, url: String) = "$url/search/album?q=$q&uid=1&sort="
}
