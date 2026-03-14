package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.Product
import java.net.URI

data class ProductEmbedded(
    val name: String,
    val price: String?,
    val productLink: String,
    val imageLink: String,
) {
    fun toDomain() =
        Product(
            name = name,
            price = price,
            productLink = URI(productLink),
            imageLink = URI(imageLink),
        )

    companion object {
        fun from(product: Product) =
            ProductEmbedded(
                name = product.name,
                price = product.price,
                productLink = product.productLink.toString(),
                imageLink = product.imageLink.toString(),
            )
    }
}
