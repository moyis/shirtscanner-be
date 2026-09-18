package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.SearchResultCacheConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.repositories.search.SearchResultDocument
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.stereotype.Component

private val LOG = KotlinLogging.logger {}

@Component
class SearchResultIndexInitializer(
    private val mongoTemplate: MongoTemplate,
    private val searchResultCacheConfigurationProperties: SearchResultCacheConfigurationProperties,
) {
    @PostConstruct
    fun ensureIndexes() {
        try {
            mongoTemplate
                .indexOps(SearchResultDocument::class.java)
                .createIndex(
                    Index()
                        .on("createdAt", Sort.Direction.ASC)
                        .expire(searchResultCacheConfigurationProperties.ttl.toSeconds()),
                )
        } catch (e: DataAccessException) {
            LOG.warn(e) { "Skipping search-result TTL index creation; MongoDB unavailable" }
        }
    }
}