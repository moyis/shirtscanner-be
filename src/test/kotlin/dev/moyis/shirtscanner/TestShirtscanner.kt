package dev.moyis.shirtscanner

import dev.moyis.shirtscanner.testsupport.RedisTestcontainersConfiguration
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<Shirtscanner>()
        .with(RedisTestcontainersConfiguration::class)
        .run(*args)
}
