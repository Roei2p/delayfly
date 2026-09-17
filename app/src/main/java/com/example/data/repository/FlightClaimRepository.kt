package com.example.data.repository

import com.example.data.dao.FlightClaimDao
import com.example.data.model.FlightClaim
import kotlinx.coroutines.flow.Flow

interface FlightClaimRepository {
    fun getAllClaims(): Flow<List<FlightClaim>>
    fun getClaimById(id: Long): Flow<FlightClaim?>
    suspend fun insertClaim(claim: FlightClaim): Long
    suspend fun updateClaim(claim: FlightClaim)
    suspend fun deleteClaimById(id: Long)
}

class FlightClaimRepositoryImpl(
    private val dao: FlightClaimDao
) : FlightClaimRepository {
    override fun getAllClaims(): Flow<List<FlightClaim>> = dao.getAllClaims()

    override fun getClaimById(id: Long): Flow<FlightClaim?> = dao.getClaimById(id)

    override suspend fun insertClaim(claim: FlightClaim): Long = dao.insertClaim(claim)

    override suspend fun updateClaim(claim: FlightClaim) = dao.updateClaim(claim)

    override suspend fun deleteClaimById(id: Long) = dao.deleteClaimById(id)
}
