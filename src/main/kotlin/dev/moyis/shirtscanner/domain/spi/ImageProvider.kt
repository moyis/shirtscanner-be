package dev.moyis.shirtscanner.domain.spi

interface ImageProvider {
    suspend fun get(path: String): ByteArray?
}
