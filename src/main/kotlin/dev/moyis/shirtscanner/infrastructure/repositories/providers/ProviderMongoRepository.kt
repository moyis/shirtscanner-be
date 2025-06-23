package dev.moyis.shirtscanner.infrastructure.repositories.providers

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProviderMongoRepository : MongoRepository<ProviderDocument, String>
