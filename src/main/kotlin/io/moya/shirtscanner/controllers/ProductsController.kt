package io.moya.shirtscanner.controllers

import io.moya.shirtscanner.services.ProductsService
import org.springframework.web.bind.annotation.CrossOrigin
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

    @GetMapping("/stream")
    @CrossOrigin(origins = ["*"])
    fun searchStream(
        @RequestParam("q") q: String,
    ) = productsService.searchStream(q)
}
