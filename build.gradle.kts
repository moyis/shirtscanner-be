import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    val kotlinVersion = "2.4.20"
    val springVersion = "4.1.1"
    val springDependencyManagementVersion = "1.1.7"
    val nativeVersion = "1.1.14"
    val testLoggerVersion = "4.0.0"
    val pitestVersion = "1.19.0"

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
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

val jsoupVersion = "1.23.2"
val kotlinLoggingVersion = "3.0.5"
val mockitoKotlinVersion = "6.3.0"
val restAssuredVersion = "6.0.1"
val wiremockVersion = "4.2.3"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Jackson
    implementation("tools.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Logging
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    // Scraping
    implementation("org.jsoup:jsoup:$jsoupVersion")

    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Spring
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")

    // Wiremock
    testImplementation("org.wiremock.integrations:wiremock-spring-boot:$wiremockVersion")

    // Testconainters
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mongodb")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Kotlin
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // CVE-2026-55760 (GHSA-r4gv-qr8j-p3pg): handlebars.java FileTemplateLoader path traversal.
    // Test-only, transitively pulled in by WireMock at 4.3.1. Declared directly so every configuration
    // resolves 4.5.5 (highest version wins), including the Pitest plugin's detached tmpTestImplementation
    // snapshot, which a container-level force/constraint cannot reach.
    testImplementation("com.github.jknack:handlebars:4.5.5")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<JavaExec>("processAot") {
    jvmArgs("--add-opens", "java.base/java.net=ALL-UNNAMED")
}

tasks.named<JavaExec>("processTestAot") {
    jvmArgs("--add-opens", "java.base/java.net=ALL-UNNAMED")
}

pitest {
    pitestVersion = "1.30.0"
    junit5PluginVersion = "1.2.3"
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
    builder = "paketobuildpacks/builder-jammy-tiny"
    environment.putAll(
        mapOf(
            "BP_JVM_VERSION" to "25",
            "BP_JLINK_ENABLED" to "true",
            "BP_NATIVE_IMAGE" to "true",
            "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "--gc=serial",
        ),
    )
}
