package dev.moyis.shirtscanner.infrastructure.repositories.search

import dev.moyis.shirtscanner.domain.model.ProviderName
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchResultMongoDbRepository : MongoRepository<SearchResultDocument, String> {
    fun findByProviderNameAndQuery(
        providerName: ProviderName,
        query: String,
    ): SearchResultDocument?
}
