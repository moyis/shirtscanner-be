package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.infrastructure.configuration.properties.DocumentFetcherConfigurationProperties
import mu.KotlinLogging
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import java.net.SocketTimeoutException
import java.net.URI

private val LOG = KotlinLogging.logger { }

@Service
class DocumentFetcher(
    private val configuration: DocumentFetcherConfigurationProperties,
) {
    fun fetchDocument(uri: URI): Document? =
        runCatching { doFetchDocument(uri) }
            .getOrElse { handleException(it, uri) }

    private fun doFetchDocument(uri: URI): Document =
        Jsoup
            .connect(uri.toASCIIString())
            .timeout(configuration.defaultTimeout.toMillis().toInt())
            .headers(DEFAULT_HEADERS)
            .get()

    private fun handleException(
        throwable: Throwable,
        url: URI,
    ): Document? {
        val baseMessage = "Exception found performing get on $url:"
        when (throwable) {
            is HttpStatusException -> {
                LOG.error { throwable.stackTraceToString() }
                LOG.warn { "$baseMessage returned status code ${throwable.statusCode} ${throwable.message}" }
            }

            is SocketTimeoutException -> {
                LOG.warn { "$baseMessage took more than ${configuration.defaultTimeout.toMillis()}" }
            }

            is UnsupportedMimeTypeException -> {
                LOG.warn { "$baseMessage returned unsupported mime type ${throwable.mimeType}" }
            }

            else -> LOG.error { "Unexpected exception found while performing get on $url: ${throwable.cause}" }
        }
        return null
    }

    companion object {
        private val DEFAULT_HEADERS =
            mapOf(
                "Dnt" to "1",
                "Sec-Ch-Ua" to """"Not_A Brand";v="8", "Chromium";v="120"""",
                "Sec-Ch-Ua-Mobile" to "?0",
                "Sec-Ch-Ua-Platform" to "\"macOS\"",
                "Sec-Fetch-Dest" to "document",
                "Sec-Fetch-Mode" to "navigate",
                "Sec-Fetch-Site" to "same-origin",
                "Sec-Fetch-User" to "?1",
                "Upgrade-Insecure-Requests" to "1",
                HttpHeaders.USER_AGENT to
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/120.0.0.0 Safari/537.36",
            )
    }
}
