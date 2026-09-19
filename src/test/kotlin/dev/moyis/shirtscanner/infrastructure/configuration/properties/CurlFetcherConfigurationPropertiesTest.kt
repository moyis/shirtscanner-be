package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import java.time.Duration

class CurlFetcherConfigurationPropertiesTest {

    private val contextRunner =
        ApplicationContextRunner()
            .withUserConfiguration(CurlFetcherConfigurationPropertiesTestConfiguration::class.java)

    @Test
    fun `binds configured values`() {
        contextRunner
            .withPropertyValues(
                "fetchers.curl.binary=/tmp/fake-curl",
                "fetchers.curl.default-timeout=7s",
                "fetchers.curl.attempts=3",
                "fetchers.curl.backoff=200ms",
            )
            .run { context ->
                val properties = context.getBean(CurlFetcherConfigurationProperties::class.java)
                assertThat(properties.binary).isEqualTo("/tmp/fake-curl")
                assertThat(properties.defaultTimeout).isEqualTo(Duration.ofSeconds(7))
                assertThat(properties.attempts).isEqualTo(3)
                assertThat(properties.backoff).isEqualTo(Duration.ofMillis(200))
            }
    }

    @Test
    fun `applies defaults when unconfigured`() {
        contextRunner.run { context ->
            val properties = context.getBean(CurlFetcherConfigurationProperties::class.java)
            assertThat(properties.binary).isEqualTo("/usr/local/bin/curl-impersonate")
            assertThat(properties.defaultTimeout).isEqualTo(Duration.ofSeconds(20))
            assertThat(properties.attempts).isEqualTo(5)
            assertThat(properties.backoff).isEqualTo(Duration.ofMillis(500))
        }
    }

    @EnableConfigurationProperties(CurlFetcherConfigurationProperties::class)
    class CurlFetcherConfigurationPropertiesTestConfiguration
}