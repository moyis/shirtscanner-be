package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.api.ImageService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/v1/images")
class ImageController(
    private val imageService: ImageService,
) {
    @GetMapping("/yupoo")
    fun proxyImage(
        @RequestParam("path") path: String,
    ): Flux<DataBuffer> = imageService.get(path)
}
