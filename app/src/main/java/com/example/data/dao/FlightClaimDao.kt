package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FlightClaim
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightClaimDao {
    @Query("SELECT * FROM flight_claims ORDER BY createdAt DESC")
    fun getAllClaims(): Flow<List<FlightClaim>>

    @Query("SELECT * FROM flight_claims WHERE id = :id")
    fun getClaimById(id: Long): Flow<FlightClaim?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: FlightClaim): Long

    @Update
    suspend fun updateClaim(claim: FlightClaim)

    @Delete
    suspend fun deleteClaim(claim: FlightClaim)

    @Query("DELETE FROM flight_claims WHERE id = :id")
    suspend fun deleteClaimById(id: Long)
}
