package io.moya.shirtscanner.controllers

import io.moya.shirtscanner.services.providers.ProviderService
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
    fun findAll() = providerService.getProviders()

    @PostMapping
    fun status(): ResponseEntity<Nothing> {
        providerService.checkStatus()
        return ResponseEntity.ok().build()
    }
}
