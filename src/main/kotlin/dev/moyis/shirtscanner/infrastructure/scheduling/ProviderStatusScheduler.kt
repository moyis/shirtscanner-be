package dev.moyis.shirtscanner.infrastructure.scheduling

import dev.moyis.shirtscanner.domain.api.ProviderService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProviderStatusScheduler(
    private val providerService: ProviderService,
) {
    @Scheduled(cron = "0 0 * * * *")
    fun refreshProviderStatus() {
        providerService.checkStatus()
    }
}