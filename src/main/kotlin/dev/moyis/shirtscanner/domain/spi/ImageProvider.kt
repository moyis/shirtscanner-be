package dev.moyis.shirtscanner.domain.spi

import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

fun interface ImageProvider {
    fun get(path: String): Flux<DataBuffer>
}
