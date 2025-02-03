package dev.moyis.shirtscanner

import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["dev.moyis.shirtscanner.infrastructure.configuration.properties"])
@RegisterReflection(classNames = ["com.github.benmanes.caffeine.cache.SSSW"])
class Shirtscanner

fun main(args: Array<String>) {
    runApplication<Shirtscanner>(*args)
}
