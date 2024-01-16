package io.moya.shirtscanner.services.fetchers

import io.moya.shirtscanner.models.Product
import io.moya.shirtscanner.models.SearchResult
import io.moya.shirtscanner.services.WebConnector
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class ListR1Fetcher(
    private val webConnector: WebConnector,
) : ProductsFetcher {
    override fun search(
        q: String,
        url: String,
    ): SearchResult {
        val products = getProducts(q, url)
        return SearchResult(
            queryUrl = getWebpageUrl(q, url),
            products = products,
        )
    }

    private fun getDocuments(
        q: String,
        url: String,
    ) = generateSequence(1) { it + 1 }
        .map { getWebpageUrl(q, url, it) }
        .take(2)
        .mapNotNull { webConnector.fetchDocument(it) }
        .takeWhile { it.getElementsByClass("empty__emptytitle").isEmpty() }

    private fun getProducts(
        q: String,
        url: String,
    ) = getDocuments(q, url)
        .flatMap { it.select("li") }
        .mapNotNull { mapToProduct(it, url) }
        .distinct()
        .toList()

    private fun getWebpageUrl(
        q: String,
        url: String,
        pageNumber: Int = 1,
    ) = """$url/Search-$q/list--1000-1-2-----r$pageNumber.html"""

    private fun mapToProduct(
        element: Element,
        baseUrl: String,
    ): Product? {
        val a = element.selectFirst("a") ?: return null
        val price = element.getElementsByClass("price").first() ?: return null
        val imageLink = element.selectFirst("img")?.attr("src") ?: return null
        val productLink = a.attr("href")
        val name = a.attr("title")
        return Product(
            name = name,
            price = price.text(),
            productLink = "$baseUrl$productLink",
            imageLink = imageLink,
        )
    }
}
