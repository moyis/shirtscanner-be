package dev.moyis.shirtscanner.infrastructure.configuration.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class FetchersConfigurationPropertiesTest {

    private val contextRunner =
        ApplicationContextRunner()
            .withUserConfiguration(FetchersConfigurationPropertiesTestConfiguration::class.java)

    @Test
    fun `browserTls defaults to false`() {
        contextRunner
            .withPropertyValues(
                "fetchers.list-r-1[0].url=https://www.a.com",
                "fetchers.list-r-1[0].name=A",
                "fetchers.yupoo[0].url=https://x.x.yupoo.com",
                "fetchers.yupoo[0].name=Y",
            )
            .run { context ->
                val properties = context.getBean(FetchersConfigurationProperties::class.java)
                assertThat(properties.listR1.single().browserTls).isFalse()
            }
    }

    @Test
    fun `binds browserTls`() {
        contextRunner
            .withPropertyValues(
                "fetchers.list-r-1[0].url=https://www.a.com",
                "fetchers.list-r-1[0].name=A",
                "fetchers.list-r-1[0].browser-tls=true",
                "fetchers.yupoo[0].url=https://x.x.yupoo.com",
                "fetchers.yupoo[0].name=Y",
            )
            .run { context ->
                val properties = context.getBean(FetchersConfigurationProperties::class.java)
                assertThat(properties.listR1.single().browserTls).isTrue()
            }
    }

    @EnableConfigurationProperties(FetchersConfigurationProperties::class)
    class FetchersConfigurationPropertiesTestConfiguration
}