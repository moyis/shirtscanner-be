package dev.moyis.shirtscanner.infrastructure.controllers

import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(ImageController::class)
@WireMockTest
class ImageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc


}