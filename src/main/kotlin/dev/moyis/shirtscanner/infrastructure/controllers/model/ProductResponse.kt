package dev.moyis.shirtscanner.infrastructure.controllers.model

import dev.moyis.shirtscanner.domain.model.Product
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import java.net.URI

@RegisterReflectionForBinding
data class ProductResponse(
    val name: String,
    val price: String?,
    val productLink: URI,
    val imageLink: URI,
) {
    companion object {
        fun fromProduct(product: Product): ProductResponse =
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
