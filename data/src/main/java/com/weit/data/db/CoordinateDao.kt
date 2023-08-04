package com.weit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weit.data.model.coordinate.Coordinate

@Dao
interface CoordinateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoordinate(coordinate: Coordinate)

    @Query("DELETE FROM coordinate WHERE id=:id")
    suspend fun deleteCoordinate(id: Long)

    @Query("SELECT * FROM coordinate WHERE time BETWEEN :startTime AND :endTime")
    suspend fun getCoordinates(startTime: Long, endTime: Long): List<Coordinate>
}
