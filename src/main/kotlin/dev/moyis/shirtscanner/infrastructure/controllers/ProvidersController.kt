package dev.moyis.shirtscanner.infrastructure.controllers

import dev.moyis.shirtscanner.domain.api.ProviderService
import dev.moyis.shirtscanner.infrastructure.controllers.model.ProviderResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/providers")
class ProvidersController(
    private val providerService: ProviderService,
) {
    @GetMapping
    fun findAll() = providerService.findAll().map { ProviderResponse.fromProvider(it) }

    @PostMapping
    fun status(): ResponseEntity<Nothing> {
        providerService.checkStatus()
        return ResponseEntity.ok().build()
    }
}
