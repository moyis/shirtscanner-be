package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.infrastructure.configuration.properties.FetchersConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.configuration.properties.YupooProviderConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.services.DocumentFetcher
import dev.moyis.shirtscanner.infrastructure.services.ListR1ProductProvider
import dev.moyis.shirtscanner.infrastructure.services.YupooProductProvider
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val LOG = KotlinLogging.logger { }

@Configuration
class ProviderConfiguration(
    private val config: FetchersConfigurationProperties,
    private val documentFetcher: DocumentFetcher,
    private val yupooProviderConfigurationProperties: YupooProviderConfigurationProperties,
) {
    @Bean
    fun productProviders(): List<ProductProvider> {
        val listR1ProductProviders = getListR1ProductProviders()
        val yupooFetcherProviders = getYupooProductProviders()
        return listR1ProductProviders + yupooFetcherProviders
    }

    private fun getListR1ProductProviders(): List<ProductProvider> {
        val listR1ProductProvider =
            config.listR1.map {
                ListR1ProductProvider(
                    name = ProviderName(it.name),
                    url = it.url,
                    documentFetcher = documentFetcher,
                )
            }
        LOG.info { "Initialized ${listR1ProductProvider.size} ListR1 product providers" }
        return listR1ProductProvider
    }

    private fun getYupooProductProviders(): List<ProductProvider> {
        val yupooProvider =
            config.yupoo.map {
                YupooProductProvider(
                    name = ProviderName(it.name),
                    url = it.url,
                    documentFetcher = documentFetcher,
                    configuration = yupooProviderConfigurationProperties,
                )
            }
        LOG.info { "Found ${yupooProvider.size} providers using Yupoo fetcher" }
        return yupooProvider
    }
}
