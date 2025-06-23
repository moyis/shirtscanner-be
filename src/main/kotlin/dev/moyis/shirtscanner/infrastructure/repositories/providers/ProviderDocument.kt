package dev.moyis.shirtscanner.infrastructure.repositories.providers

import dev.moyis.shirtscanner.domain.model.Provider
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.ProviderStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URI
import java.time.LocalDateTime

@Document("providers")
data class ProviderDocument(
    @Id
    val id: String,
    val url: String,
    val status: ProviderStatus,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(
            provider: Provider,
            createdAt: LocalDateTime,
        ) = with(provider) {
            ProviderDocument(
                id = name.value,
                url = "$url",
                status = status,
                createdAt = createdAt,
            )
        }
    }

    fun toDomain() =
        Provider(
            name = ProviderName(id),
            url = URI(url),
            status = status,
        )
}
