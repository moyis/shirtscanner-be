package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import java.net.URI

@RegisterReflectionForBinding
data class Product(
    val name: String,
    val price: String?,
    val productLink: URI,
    val imageLink: URI,
)
