package dev.moyis.shirtscanner.infrastructure.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class CurlProcessRunnerTest {

    @TempDir
    lateinit var tempDir: Path

    private val runner = CurlProcessRunner()
    private lateinit var fakeOk: Path
    private lateinit var fake403: Path
    private lateinit var fakeTimeout: Path
    private lateinit var fakeEmpty: Path

    @BeforeEach
    fun setUp() {
        fakeOk = tempDir.resolve("fake-ok.sh")
        fake403 = tempDir.resolve("fake-403.sh")
        fakeTimeout = tempDir.resolve("fake-timeout.sh")
        fakeEmpty = tempDir.resolve("fake-empty.sh")
        writeScript(
            fakeOk,
            """
            #!/usr/bin/env bash
            headers="${'$'}1"
            body="${'$'}2"
            printf 'HTTP/1.1 200 OK\r\n\r\n' > "${'$'}headers"
            printf '%s' "ok" > "${'$'}body"
            exit 0
            """,
        )
        writeScript(
            fake403,
            """
            #!/usr/bin/env bash
            headers="${'$'}1"
            body="${'$'}2"
            printf 'HTTP/1.1 403 Forbidden\r\n\r\n' > "${'$'}headers"
            printf '%s' "Just a moment..." > "${'$'}body"
            exit 0
            """,
        )
        writeScript(
            fakeTimeout,
            """
            #!/usr/bin/env bash
            exit 28
            """,
        )
        writeScript(
            fakeEmpty,
            """
            #!/usr/bin/env bash
            exit 0
            """,
        )
    }

    @Test
    fun `parses status code and body`() {
        val headers = tempDir.resolve("h.txt")
        val body = tempDir.resolve("b.html")
        val result = runner.run(listOf(fakeOk.toString(), headers.toString(), body.toString()), headers, body)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.statusCode).isEqualTo(200)
        assertThat(result.body).isEqualTo("ok")
    }

    @Test
    fun `parses 403 challenge`() {
        val headers = tempDir.resolve("h.txt")
        val body = tempDir.resolve("b.html")
        val result = runner.run(listOf(fake403.toString(), headers.toString(), body.toString()), headers, body)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.statusCode).isEqualTo(403)
        assertThat(result.body).isEqualTo("Just a moment...")
    }

    @Test
    fun `returns null status and body when output files are missing`() {
        val headers = tempDir.resolve("h.txt")
        val body = tempDir.resolve("b.html")
        val result = runner.run(listOf(fakeEmpty.toString(), headers.toString(), body.toString()), headers, body)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.statusCode).isNull()
        assertThat(result.body).isNull()
    }

    @Test
    fun `propagates curl exit code when it fails before writing output`() {
        val headers = tempDir.resolve("h.txt")
        val body = tempDir.resolve("b.html")
        val result = runner.run(listOf(fakeTimeout.toString(), headers.toString(), body.toString()), headers, body)

        assertThat(result.exitCode).isEqualTo(28)
        assertThat(result.statusCode).isNull()
        assertThat(result.body).isNull()
    }

    private fun writeScript(path: Path, content: String) {
        Files.writeString(path, content)
        path.toFile().setExecutable(true)
    }
}
