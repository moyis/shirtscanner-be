package io.moya.shirtscanner.services.providers

import io.moya.shirtscanner.configuration.ProviderData
import io.moya.shirtscanner.services.cache.CacheService
import io.moya.shirtscanner.services.providers.model.ProviderResponse
import io.moya.shirtscanner.services.providers.model.ProviderStatus
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.Executors

private val LOG = KotlinLogging.logger { }

@Service
class ProviderService(
    private val providers: List<ProviderData>,
    private val cacheService: CacheService,
    @Qualifier("webConnectorRetryTemplate")
    private val retryTemplate: RetryTemplate,
) {
    private val executorService = Executors.newVirtualThreadPerTaskExecutor()

    fun getProviders(): List<ProviderResponse> {
        val status = cacheService.getAll<ProviderStatus>(*providers.map { cacheKey(it.name) }.toTypedArray())
        return providers.map {
            ProviderResponse(
                name = it.name,
                status = status[cacheKey(it.name)] ?: ProviderStatus.UNKNOWN,
                url = it.url,
            )
        }
    }

    private fun cacheKey(name: String) = "status_${name.replace(" ", "_")}"

    fun checkStatus() =
        executorService.execute {
            LOG.info { "Started providers status check" }
            providers.asSequence()
                .map { it.name to getProviderStatus(it) }
                .forEach { (name, status) -> cacheService.set("status_$name", status) }
            LOG.info { "Finished providers status check" }
        }

    private fun getProviderStatus(providerData: ProviderData): ProviderStatus {
        LOG.info { "Started provider check for ${providerData.name}" }
        val statusCode = getStatusCode(providerData.url)

        return when (statusCode) {
            in 200..299 -> ProviderStatus.UP
            in 400..599, null -> ProviderStatus.DOWN
            else -> ProviderStatus.UNKNOWN
        }.also { LOG.info { "Finished provider check for ${providerData.name} with result $it" } }
    }

    private fun getStatusCode(url: String) =
        runCatching {
            retryTemplate.execute<Int, Throwable> {
                Jsoup.connect(url)
                    .timeout(Duration.ofSeconds(15).toMillis().toInt())
                    .execute()
                    .statusCode()
            }
        }.getOrNull()
}
