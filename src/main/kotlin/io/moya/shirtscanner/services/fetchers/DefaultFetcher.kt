package io.moya.shirtscanner.services.fetchers

import io.moya.shirtscanner.models.Product
import io.moya.shirtscanner.models.SearchResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class DefaultFetcher(
    private val url: String,
) : ProductsFetcher {
    override fun search(q: String): SearchResult {
        val query = """$url/Search-$q/list--1000-1-2-----r1.html"""
        val doc = Jsoup.connect(query).get()
        val products = doc.select("li")
            .asSequence()
            .mapNotNull { mapToProduct(it) }
            .toList()

        return SearchResult(
            queryUrl = query,
            products = products,
        )
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
}
