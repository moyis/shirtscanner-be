package dev.moyis.shirtscanner.infrastructure.repositories

import dev.moyis.shirtscanner.domain.model.Product
import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import java.net.URI

class RedissonSearchResultRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var repository: RedissonSearchResultRepository

    @Autowired
    private lateinit var redisson: RedissonClient

    @Test
    fun `saves result if absent`() {
        repository.computeIfAbsent(
            providerName = ProviderName("testProvider"),
            query = "argentina",
        ) {
            SearchResult(
                providerName = "testProvider",
                queryUrl = URI("https://localhost:1234?q=argentina"),
            )
        }

        assertThat(redisson.getBucket<SearchResult>("results-testProvider-argentina").get()).isNotNull
    }

    @Test
    fun `returns result from redis when value present`() {
        tfs.persistSearchResult(ProviderName("testProvider"), query = "argentina", aValidSearchResult())
        val result =
            repository.computeIfAbsent(
                providerName = ProviderName("testProvider"),
                query = "argentina",
            ) {
                aValidSearchResult(products = listOf(aValidProduct()))
            }

        assertThat(result.products).isEmpty()
    }

    @Test
    fun `does not execute function when value is present`() {
        var ifAbsentExecuted = false

        tfs.persistSearchResult(ProviderName("testProvider"), query = "argentina", aValidSearchResult())
        repository.computeIfAbsent(
            providerName = ProviderName("testProvider"),
            query = "argentina",
        ) {
            ifAbsentExecuted = true
            aValidSearchResult(products = listOf(aValidProduct()))
        }
        assertThat(ifAbsentExecuted).isFalse
    }

    private fun aValidProduct(): Product =
        Product(
            name = "Argentina Shirt 2025",
            price = "U\$S 15,24",
            productLink = URI("https://localhost:1234/argentina"),
            imageLink = URI("https://localhost:1234/argentina.jpg"),
        )

    private fun aValidSearchResult(products: List<Product> = emptyList()): SearchResult =
        SearchResult(
            providerName = "savedProvider",
            queryUrl = URI("https://localhost:1234?q=argentina"),
            products = products,
        )
}
