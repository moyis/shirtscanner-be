package dev.moyis.shirtscanner.domain.api

import dev.moyis.shirtscanner.domain.spi.ImageProvider
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ImageService(
    private val imageProvider: ImageProvider,
) {
    fun get(path: String): Flux<DataBuffer> = imageProvider.get(path)
}
