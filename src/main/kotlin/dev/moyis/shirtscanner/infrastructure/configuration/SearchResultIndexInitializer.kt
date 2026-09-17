package dev.moyis.shirtscanner.infrastructure.configuration

import dev.moyis.shirtscanner.infrastructure.configuration.properties.SearchResultCacheConfigurationProperties
import dev.moyis.shirtscanner.infrastructure.repositories.search.SearchResultDocument
import jakarta.annotation.PostConstruct
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.stereotype.Component

@Component
class SearchResultIndexInitializer(
    private val mongoTemplate: MongoTemplate,
    private val searchResultCacheConfigurationProperties: SearchResultCacheConfigurationProperties,
) {
    @PostConstruct
    fun ensureIndexes() {
        mongoTemplate
            .indexOps(SearchResultDocument::class.java)
            .createIndex(
                Index()
                    .on("createdAt", Sort.Direction.ASC)
                    .expire(searchResultCacheConfigurationProperties.ttl.toSeconds()),
            )
    }
}