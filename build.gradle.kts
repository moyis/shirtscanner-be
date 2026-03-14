import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    val kotlinVersion = "2.3.10"
    val springVersion = "4.0.3"
    val springDependencyManagementVersion = "1.1.7"
    val nativeVersion = "0.11.4"
    val testLoggerVersion = "4.0.0"
    val pitestVersion = "1.19.0-rc.3"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version springVersion
    id("io.spring.dependency-management") version springDependencyManagementVersion
   // id("org.graalvm.buildtools.native") version nativeVersion

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

val jsoupVersion = "1.22.1"
val kotlinLoggingVersion = "3.0.5"
val mockitoKotlinVersion = "6.2.3"
val restAssuredVersion = "6.0.0"
val wiremockVersion = "4.0.9"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Jackson
    implementation("tools.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Logging
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Scraping
    implementation("org.jsoup:jsoup:$jsoupVersion")

    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Spring
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

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
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
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
            "BP_JVM_VERSION" to "25",
            "BP_JVM_CDS_ENABLED" to "true",
            "BP_JLINK_ENABLED" to "true",
            "SPRING_DATA_MONGODB_AUTO_INDEX_CREATION" to "false",
        ),
    )
}
