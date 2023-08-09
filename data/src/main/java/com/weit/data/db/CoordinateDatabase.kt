package com.weit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weit.data.model.coordinate.Coordinate

@Database(
    entities = [Coordinate::class],
    version = 1,
    exportSchema = false,
)
abstract class CoordinateDatabase : RoomDatabase() {
    abstract fun coordinateDao(): CoordinateDao
}
