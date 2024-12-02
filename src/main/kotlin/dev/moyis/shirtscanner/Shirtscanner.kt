package dev.moyis.shirtscanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Shirtscanner

fun main(args: Array<String>) {
    runApplication<Shirtscanner>(*args)
}
