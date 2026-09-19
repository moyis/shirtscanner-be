package dev.moyis.shirtscanner.infrastructure.services

import org.jsoup.nodes.Document
import java.net.URI

interface PageFetcher {
    fun fetchDocument(uri: URI): Document?
}
