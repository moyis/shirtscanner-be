package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.api.ImageService
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors

@RestController
@RequestMapping("/v1/images")
class ImageController(
    private val imageService: ImageService,
) {
    private val dispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    @GetMapping("/yupoo")
    suspend fun proxyImage(
        @RequestParam("path") path: String,
    ): ResponseEntity<ByteArray> =
        withContext(dispatcher) {
            ResponseEntity.ofNullable(imageService.get(path))
        }
}
