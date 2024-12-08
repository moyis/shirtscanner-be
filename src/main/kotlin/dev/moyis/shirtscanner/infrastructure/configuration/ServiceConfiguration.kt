package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.domain.api.ImageService
import dev.moyis.shirtscanner.domain.api.ProductService
import dev.moyis.shirtscanner.domain.api.ProviderService
import dev.moyis.shirtscanner.domain.spi.ImageProvider
import dev.moyis.shirtscanner.domain.spi.ProductProvider
import dev.moyis.shirtscanner.domain.spi.ProviderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration(
    private val productProviders: List<ProductProvider>,
    private val providerRepository: ProviderRepository,
    private val imageProvider: ImageProvider,
) {
    @Bean
    fun productService(): ProductService =
        ProductService(
            productProviders = productProviders,
        )

    @Bean
    fun providerService(): ProviderService =
        ProviderService(
            productProviders = productProviders,
            providerRepository = providerRepository,
        )

    @Bean
    fun imageService(): ImageService =
        ImageService(
            imageProvider = imageProvider,
        )
}
