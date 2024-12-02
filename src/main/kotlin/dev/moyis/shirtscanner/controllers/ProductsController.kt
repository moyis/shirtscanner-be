package dev.moyis.shirtscanner.controllers

import dev.moyis.shirtscanner.services.ProductsService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/products")
class ProductsController(
    private val productsService: ProductsService,
) {
    @GetMapping
    fun search(
        @RequestParam("q") q: String,
    ) = productsService.search(q)

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun searchStream(
        @RequestParam("q") q: String,
    ) = productsService.searchStream(q)
}
