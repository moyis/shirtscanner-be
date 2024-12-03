package dev.moyis.shirtscanner.infrastructure.controllers

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@RestController
@RequestMapping("/v1/images")
class ImageController(
    @Qualifier("imageProxyRetryTemplate")
    private val retryTemplate: RetryTemplate,
) {
    private val restClient = RestClient.create()

    @GetMapping("/yupoo")
    fun proxyImage(
        @RequestParam("path") path: String,
    ): ByteArray? = retryTemplate.execute<ByteArray?, Exception> { getImageBytes(path) }

    private fun getImageBytes(path: String) =
        restClient
            .get()
            .uri("https://photo.yupoo.com/$path")
            .header(HttpHeaders.REFERER, "https://yupoo.com/")
            .retrieve()
            .body<ByteArray>()
}
