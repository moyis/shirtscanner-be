package io.moya.shirtscanner.controllers

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@RestController
@RequestMapping("/v1/images")
class ImageController {

    private val restClient = RestClient.create()

    @GetMapping("/yupoo")
    fun proxyImage(@RequestParam("path") path: String) = restClient.get()
        .uri("https://photo.yupoo.com/$path")
        .header(HttpHeaders.REFERER, "https://yupoo.com/")
        .retrieve()
        .toEntity(ByteArray::class.java)
}