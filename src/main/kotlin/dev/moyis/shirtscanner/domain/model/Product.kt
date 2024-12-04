package dev.moyis.shirtscanner.domain.model

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding

@RegisterReflectionForBinding
data class Product(
    val name: String,
    val price: String?,
    val productLink: String,
    val imageLink: String,
)
