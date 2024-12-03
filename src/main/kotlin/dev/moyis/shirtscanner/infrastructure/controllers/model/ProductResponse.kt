package dev.moyis.shirtscanner.infrastructure.controllers.model

import dev.moyis.shirtscanner.domain.model.Product
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class ProductResponse(
    val name: String,
    val price: String?,
    val productLink: String,
    val imageLink: String,
) {
    companion object {
        fun fromProduct(product: Product) =
            with(product) {
                ProductResponse(
                    name = name,
                    price = price,
                    productLink = productLink,
                    imageLink = imageLink,
                )
            }
    }
}
