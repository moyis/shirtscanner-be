package dev.moyis.shirtscanner

import dev.moyis.shirtscanner.testsupport.MongoDbTestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<Shirtscanner>()
        .with(MongoDbTestcontainersConfiguration::class)
        .run(*args)
}
