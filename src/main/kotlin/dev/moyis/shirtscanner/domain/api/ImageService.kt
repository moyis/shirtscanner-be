package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.spi.ImageProvider
import org.springframework.stereotype.Service

@Service
class ImageService(
    private val imageProvider: ImageProvider,
) {
    suspend fun get(path: String): ByteArray? = imageProvider.get(path)
}
