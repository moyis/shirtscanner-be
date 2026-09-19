package dev.moyis.shirtscanner.infrastructure.services

import dev.moyis.shirtscanner.infrastructure.configuration.properties.CurlFetcherConfigurationProperties
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.net.URI
import java.nio.file.Files
import kotlin.random.Random

private val LOG = KotlinLogging.logger {}

class CurlImpersonatingFetcher(
    private val configuration: CurlFetcherConfigurationProperties,
    private val runner: CurlProcessRunner = CurlProcessRunner(),
    private val random: Random = Random.Default,
) : PageFetcher {

    override fun fetchDocument(uri: URI): org.jsoup.nodes.Document? =
        runCatching { fetchWithRetries(uri) }
            .getOrElse {
                LOG.error { "Unexpected exception fetching $uri with curl: ${it.message}" }
                null
            }

    private fun fetchWithRetries(uri: URI): org.jsoup.nodes.Document? {
        var attempt = 0
        while (attempt < configuration.attempts) {
            val attemptDir = Files.createTempDirectory("curl-fetch-")
            try {
                val headersFile = attemptDir.resolve("headers.txt")
                val bodyFile = attemptDir.resolve("body.html")
                val result = runner.run(buildCommand(uri, headersFile, bodyFile), headersFile, bodyFile)

                if (result.statusCode == 403 || isChallengeBody(result.body)) {
                    attempt += 1
                    if (attempt < configuration.attempts) sleepBeforeNextAttempt()
                    continue
                }
                if (result.statusCode != null && result.statusCode !in 200..299) {
                    LOG.warn { "HTTP ${result.statusCode} from $uri is not a challenge, giving up" }
                    return null
                }
                if (result.statusCode == null) {
                    LOG.warn { "Curl exited ${result.exitCode} on $uri without an HTTP status, giving up" }
                    return null
                }
                val html = result.body?.takeIf { it.isNotBlank() } ?: run {
                    LOG.warn { "Empty body from $uri, giving up" }
                    return null
                }
                return Jsoup.parse(html, uri.toASCIIString())
            } finally {
                attemptDir.toFile().deleteRecursively()
            }
        }
        LOG.warn { "Giving up on $uri after ${configuration.attempts} attempts" }
        return null
    }

    private fun isChallengeBody(body: String?): Boolean {
        if (body == null) return false
        return CHALLENGE_MARKERS.any { body.contains(it, ignoreCase = true) }
    }

    private fun sleepBeforeNextAttempt() {
        val base = configuration.backoff.toMillis()
        val maxJitter = (base * 0.3).toLong()
        val jitter = random.nextLong(-maxJitter, maxJitter + 1)
        Thread.sleep((base + jitter).coerceAtLeast(0L))
    }

    private fun buildCommand(
        uri: URI,
        headersFile: java.nio.file.Path,
        bodyFile: java.nio.file.Path,
    ): List<String> =
        listOf(
            configuration.binary,
            "--http1.1",
            "--compressed",
            "-sS",
            "--max-time",
            configuration.defaultTimeout.toSeconds().toString(),
            "-A",
            CHROME_120_USER_AGENT,
            "-D",
            headersFile.toString(),
            "-o",
            bodyFile.toString(),
            uri.toASCIIString(),
        )

    companion object {
        private val CHALLENGE_MARKERS = listOf("Just a moment", "ACCESS DENIED", "__cf_chl")
        private const val CHROME_120_USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}
