package io.moya.shirtscanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Shirtscanner

fun main(args: Array<String>) {
	runApplication<Shirtscanner>(*args)
}
