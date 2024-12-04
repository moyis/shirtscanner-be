package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.domain.api.ProductService
import dev.moyis.shirtscanner.domain.api.ProviderService
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration(
    private val productProviders: List<ProductProvider>,
    private val providerRepository: ProviderRepository,
) {
    @Bean
    fun productService() =
        ProductService(
            productProviders = productProviders,
        )

    @Bean
    fun providerService() =
        ProviderService(
            productProviders = productProviders,
            providerRepository = providerRepository,
        )
}
