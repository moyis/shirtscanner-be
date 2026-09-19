package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.SearchResultCacheConfigurationProperties
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.data.mongodb.core.MongoTemplate
import org.junit.jupiter.api.Test

class SearchResultIndexInitializerTest {
    @Test
    fun `does not fail startup when index creation fails`() {
        val mongoTemplate = mock<MongoTemplate>()
        whenever(mongoTemplate.indexOps(any<Class<*>>()))
            .thenThrow(DataAccessResourceFailureException("boom", RuntimeException("connection refused")))

        val initializer = SearchResultIndexInitializer(
            mongoTemplate,
            SearchResultCacheConfigurationProperties(),
        )

        initializer.run(DefaultApplicationArguments())
    }
}