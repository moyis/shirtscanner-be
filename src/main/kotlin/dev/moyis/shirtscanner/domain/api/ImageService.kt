package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.spi.ImageProvider

class ImageService(
    private val imageProvider: ImageProvider,
) {
    suspend fun get(path: String) = imageProvider.get(path)
}
