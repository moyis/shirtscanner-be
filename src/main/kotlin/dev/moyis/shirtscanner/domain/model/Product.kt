package dev.moyis.shirtscanner.domain.model

import java.net.URI

data class Product(
    val name: String,
    val price: String?,
    val productLink: URI,
    val imageLink: URI,
)
