package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.domain.model.Product
import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.infrastructure.configuration.properties.YupooProviderConfigurationProperties
import mu.KotlinLogging
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URI

private val LOG = KotlinLogging.logger {}

class YupooProductProvider(
    private val url: URI,
    private val name: ProviderName,
    private val documentFetcher: DocumentFetcher,
    private val configuration: YupooProviderConfigurationProperties,
) : ProductProvider {
    override fun search(query: String): SearchResult {
        val products =
            fetchDocuments(query)
                .flatMap { getProducts(it) }
                .toList()

        return SearchResult(
            providerName = name.value,
            queryUrl = getWebpageUrl(query),
            products = products,
        )
    }

    override fun providerData(): ProviderData =
        ProviderData(
            url = url,
            name = name,
        )

    override fun status(): ProviderStatus {
        LOG.info { "Checking status of $name" }
        val testDocument = documentFetcher.fetchDocument(url)
        return if (testDocument != null) ProviderStatus.UP else ProviderStatus.DOWN
    }

    private fun fetchDocuments(query: String): Sequence<Document> =
        (1..2)
            .asSequence()
            .map { getWebpageUrl(query, it) }
            .mapNotNull { documentFetcher.fetchDocument(it) }

    fun getProducts(document: Document): List<Product> =
        document
            .getElementsByClass("album__main")
            .mapNotNull { mapToProduct(it) }
            .distinct()

    private fun mapToProduct(element: Element): Product? {
        val name = element.attr("title") ?: return null
        val productLink = element.attr("href") ?: return null
        val imageLink = getImageLink(element) ?: return null
        return Product(
            price = null,
            name = name,
            imageLink = URI("${configuration.imageProxyHost}/v1/images/yupoo?path=$imageLink"),
            productLink = URI("$url$productLink"),
        )
    }

    private fun getImageLink(element: Element): String? =
        element
            .getElementsByClass("album__img")
            .first()
            ?.attr("data-src")
            ?.substringAfter("photo.yupoo.com/")

    private fun getWebpageUrl(
        query: String,
        pageNumber: Int = 1,
    ) = URI("$url/search/album?q=$query&uid=1&sort=&page=$pageNumber")
}
