package dev.moyis.shirtscanner.infrastructure.repositories.search

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchResultMongoDbRepository : MongoRepository<SearchResultDocument, String>