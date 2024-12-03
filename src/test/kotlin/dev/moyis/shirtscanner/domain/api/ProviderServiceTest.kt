package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.model.ProviderData
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.net.URI

@ExtendWith(MockitoExtension::class)
class ProviderServiceTest {
    @Mock
    private lateinit var providerRepository: ProviderRepository

    @Mock
    private lateinit var productProvider: ProductProvider

    @Nested
    inner class CheckStatusShould {
        @Test
        fun `call status for each configured provider`() {
            val providerService = ProviderService(listOf(productProvider, productProvider), providerRepository)
            doReturn(ProviderStatus.UP).whenever(productProvider).status()
            doReturn(aValidProviderData()).whenever(productProvider).providerData()

            providerService.checkStatus()

            verify(productProvider, times(2)).status()
        }
    }

    @Nested
    inner class FindAllShould {
        @Test
        fun `returns empty with no configured providers`() {
            val providerService = ProviderService(emptyList(), providerRepository)

            val updatedRecords = providerService.findAll()

            assertThat(updatedRecords).isEmpty()
        }
    }

    private fun aValidProviderData(): ProviderData = ProviderData(URI("test.com"), ProviderName("something"))
}
