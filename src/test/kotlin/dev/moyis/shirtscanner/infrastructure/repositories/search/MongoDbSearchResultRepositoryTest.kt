package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.ProviderName
import dev.moyis.shirtscanner.domain.model.SearchResult
import dev.moyis.shirtscanner.domain.spi.SearchResultRepository
import dev.moyis.shirtscanner.testsupport.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import java.net.URI
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class MongoDbSearchResultRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var searchResultRepository: SearchResultRepository

    @Autowired
    private lateinit var searchResultMongoDbRepository: SearchResultMongoDbRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Test
    fun `stores a result when absent`() {
        var providerCalls = 0

        val result =
            searchResultRepository.computeIfAbsent(ProviderName("Provider"), "query") {
                providerCalls++
                aSearchResult("Provider")
            }

        assertThat(providerCalls).isEqualTo(1)
        assertThat(result.providerName).isEqualTo("Provider")
        assertThat(searchResultMongoDbRepository.count()).isEqualTo(1)
    }

    @Test
    fun `returns cached result without invoking the provider again`() {
        var providerCalls = 0
        val search = { aSearchResult("Provider") }
        searchResultRepository.computeIfAbsent(ProviderName("Provider"), "query") {
            providerCalls++
            search()
        }

        val result = searchResultRepository.computeIfAbsent(ProviderName("Provider"), "query") {
            providerCalls++
            search()
        }

        assertThat(providerCalls).isEqualTo(1)
        assertThat(result.providerName).isEqualTo("Provider")
    }

    @Test
    fun `concurrent requests for the same query store a single document`() {
        val threads = 8
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threads)
        val results = ConcurrentLinkedQueue<String>()
        val executor = Executors.newFixedThreadPool(threads)

        (1..threads).forEach {
            executor.execute {
                startLatch.await()
                val result = searchResultRepository.computeIfAbsent(ProviderName("Provider"), "query") {
                    aSearchResult("Provider")
                }
                results.add(result.providerName)
                doneLatch.countDown()
            }
        }
        startLatch.countDown()
        doneLatch.await()
        executor.shutdown()

        assertThat(results).containsOnly("Provider")
        assertThat(searchResultMongoDbRepository.count()).isEqualTo(1)
    }

    @Test
    fun `creates a ttl index on createdAt with the configured expiry`() {
        val indexInfo = mongoTemplate.indexOps(SearchResultDocument::class.java).indexInfo

        val ttlIndex = indexInfo.single { it.name == "createdAt_1" }

        assertThat(ttlIndex.expireAfter).contains(Duration.ofHours(24))
    }

    private fun aSearchResult(providerName: String) =
        SearchResult(
            providerName = providerName,
            queryUrl = URI("https://example.com/search?q=query"),
            products = emptyList(),
        )
}