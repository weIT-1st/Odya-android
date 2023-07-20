package com.weit.data.di.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weit.data.model.Location

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Query("DELETE FROM location WHERE id=:id")
    suspend fun deleteLocation(id: Long)

    @Query("SELECT * FROM location WHERE time BETWEEN :startTime AND :endTime")
    suspend fun getLocations(startTime: Long, endTime: Long): List<Location>
}
