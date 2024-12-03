package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.domain.model.Product
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.model.SearchResult
import mu.KotlinLogging
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URI

private val LOG = KotlinLogging.logger {}

class ListR1ProductProvider(
    override val url: URI,
    override val name: ProviderName,
    private val documentFetcher: DocumentFetcher,
) : ProductProvider {
    override fun search(query: String): SearchResult {
        val products = fetchDocuments(query)
            .flatMap { getProducts(it) }
            .toList()

        return SearchResult(
            providerName = name.value,
            queryUrl = getWebpageUrl(query),
            products = products,
        )
    }

    override fun status(): ProviderStatus {
        LOG.info { "Checking status of $name" }
        val testDocument = documentFetcher.fetchDocument(url)
        return if (testDocument != null) ProviderStatus.UP else ProviderStatus.DOWN
    }

    private fun fetchDocuments(query: String): Sequence<Document> = (1..2).asSequence()
        .map { getWebpageUrl(query, it) }
        .mapNotNull { documentFetcher.fetchDocument(it) }


    fun getProducts(document: Document) = document.select("li")
        .mapNotNull { mapToProduct(it) }
        .distinct()

    private fun mapToProduct(
        element: Element,
    ): Product? {
        val anchor = element.selectFirst("a") ?: return null
        val price = element.getElementsByClass("price").first() ?: return null
        val imageLink = element.selectFirst("img")?.attr("src") ?: return null
        val productLink = anchor.attr("href")
        val name = anchor.attr("title")
        return Product(
            name = name,
            price = price.text(),
            productLink = "$url$productLink",
            imageLink = imageLink,
        )
    }

    private fun getWebpageUrl(query: String, pageNumber: Int = 1) = URI.create("$url/Search-$query/list--1000-1-2-----r$pageNumber.html")
}