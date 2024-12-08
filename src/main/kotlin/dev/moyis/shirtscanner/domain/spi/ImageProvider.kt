package dev.moyis.shirtscanner.domain.spi

fun interface ImageProvider {
    suspend fun get(path: String): ByteArray?
}
