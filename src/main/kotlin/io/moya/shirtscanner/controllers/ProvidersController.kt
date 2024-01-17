package io.moya.shirtscanner.controllers

import io.moya.shirtscanner.configuration.ProviderData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/providers")
class ProvidersController(
    private val provider: List<ProviderData>,
) {
    @GetMapping
    fun findAll() = provider
}
