package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.FetchersConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.configuration.properties.ProviderDataConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.configuration.properties.YupooProviderConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.services.CurlImpersonatingFetcher
import dev.moyis.shirtscanner.infrastructure.services.DocumentFetcher
import dev.moyis.shirtscanner.infrastructure.services.ListR1ProductProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.net.URI

class ProviderConfigurationTest {

    @Test
    fun `browserTls providers are wired to the curl fetcher`() {
        val documentFetcher = mock<DocumentFetcher>()
        val curlFetcher = mock<CurlImpersonatingFetcher>()
        val config =
            FetchersConfigurationProperties(
                listR1 =
                    listOf(
                        ProviderDataConfigurationProperties(URI("https://www.a.com"), "A", browserTls = true),
                        ProviderDataConfigurationProperties(URI("https://www.b.com"), "B", browserTls = false),
                    ),
                yupoo = emptyList(),
            )
        val yupooConfig = YupooProviderConfigurationProperties(URI("https://api.shirtscanner.com"))

        val providers =
            ProviderConfiguration(
                config = config,
                documentFetcher = documentFetcher,
                curlImpersonatingFetcher = curlFetcher,
                yupooProviderConfigurationProperties = yupooConfig,
            ).productProviders()

        val listR1Providers = providers.filterIsInstance<ListR1ProductProvider>()
        assertThat(listR1Providers).hasSize(2)
        assertThat(listR1Providers[0].fetcher).isSameAs(curlFetcher)
        assertThat(listR1Providers[1].fetcher).isSameAs(documentFetcher)
    }
}