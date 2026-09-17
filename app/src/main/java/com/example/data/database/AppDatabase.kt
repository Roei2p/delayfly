package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.dao.FlightClaimDao
import com.example.data.model.ClaimStatus
import com.example.data.model.FlightClaim
import com.example.data.model.FlightDistance

class Converters {
    @TypeConverter
    fun fromClaimStatus(status: ClaimStatus): String = status.name

    @TypeConverter
    fun toClaimStatus(value: String): ClaimStatus = try {
        ClaimStatus.valueOf(value)
    } catch (_: Exception) {
        ClaimStatus.SUBMITTED
    }

    @TypeConverter
    fun fromFlightDistance(distance: FlightDistance): String = distance.name

    @TypeConverter
    fun toFlightDistance(value: String): FlightDistance = try {
        FlightDistance.valueOf(value)
    } catch (_: Exception) {
        FlightDistance.MEDIUM
    }
}

@Database(entities = [FlightClaim::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flightClaimDao(): FlightClaimDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flight_claims.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
