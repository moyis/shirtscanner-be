package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.api.ProductService
import dev.moyis.shirtscanner.infrastructure.controllers.model.SearchResultEventResponse
import dev.moyis.shirtscanner.infrastructure.controllers.model.SearchResultResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/products")
class ProductsController(
    private val productService: ProductService,
) {
    @GetMapping
    fun search(
        @RequestParam("q") q: String,
    ) = productService.search(q)
        .map { SearchResultResponse.fromSearchResult(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun searchStream(
        @RequestParam("q") q: String,
    ) = productService.searchStream(q)
        .map { SearchResultEventResponse.fromSearchResultResponse(it) }
}
