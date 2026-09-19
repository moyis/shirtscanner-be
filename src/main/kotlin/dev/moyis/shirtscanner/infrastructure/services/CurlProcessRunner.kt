package dev.moyis.shirtscanner.infrastructure.services

import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.readText

class CurlProcessRunner {

    data class Result(
        val exitCode: Int,
        val statusCode: Int?,
        val body: String?,
    )

    fun run(
        command: List<String>,
        headersFile: Path,
        bodyFile: Path,
    ): Result {
        val process =
            ProcessBuilder(command)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
        val exited = process.waitFor(TIMEOUT_WAIT_SECONDS, TimeUnit.SECONDS)
        if (!exited) {
            process.destroyForcibly()
            return Result(exitCode = -1, statusCode = null, body = null)
        }
        val statusCode =
            try {
                headersFile.readLines().firstOrNull()?.let(::parseHttpStatus)
            } catch (_: Exception) {
                null
            }
        val body =
            try {
                if (bodyFile.exists()) bodyFile.readText() else null
            } catch (_: Exception) {
                null
            }
        return Result(
            exitCode = process.exitValue(),
            statusCode = statusCode,
            body = body,
        )
    }

    private fun parseHttpStatus(statusLine: String): Int? {
        val parts = statusLine.trim().split(" ")
        return if (parts.size >= 2) parts[1].toIntOrNull() else null
    }

    companion object {
        private const val TIMEOUT_WAIT_SECONDS = 60L
    }
}