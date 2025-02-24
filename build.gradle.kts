import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    val kotlinVersion = "2.0.10"
    val springVersion = "3.4.2"
    val springDependencyManagementVersion = "1.1.7"
    val nativeVersion = "0.10.5"
    val testLoggerVersion = "4.0.0"
    val pitestVersion = "1.15.0"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version springVersion
    id("io.spring.dependency-management") version springDependencyManagementVersion
    id("org.graalvm.buildtools.native") version nativeVersion

    id("com.adarshr.test-logger") version testLoggerVersion
    id("info.solidsoft.pitest") version pitestVersion
}

group = "dev.moyis"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val mockitoKotlinVersion = "5.4.0"
val restAssuredVersion = "5.5.0"
val wiremockVersion = "3.11.0"
val wiremockTestContainersVersion = "1.0-alpha-14"
val kotlinLoggingVersion = "3.0.5"
val jsoupVersion = "1.18.3"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Logging
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine")

    // Scraping
    implementation("org.jsoup:jsoup:$jsoupVersion")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
    testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:$wiremockTestContainersVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

pitest {
    pitestVersion = "1.17.2"
    junit5PluginVersion = "1.2.1"
    threads = 4
    targetClasses =
        listOf(
            "dev.moyis.shirtscanner.domain.*",
            "dev.moyis.shirtscanner.infrastructure.controllers.*",
            "dev.moyis.shirtscanner.infrastructure.services.*",
            "dev.moyis.shirtscanner.infrastructure.repositories.*",
        )
}

tasks.withType<BootBuildImage> {
    environment.putAll(
        mapOf(
            "BP_JVM_VERSION" to "21",
            "BP_JVM_CDS_ENABLED" to "true",
            "CDS_TRAINING_JAVA_TOOL_OPTIONS" to "-Dspring.flyway.enabled=false",
        ),
    )
}

detekt {
    parallel = true
    autoCorrect = true
    config = files("detekt.yml")
}
