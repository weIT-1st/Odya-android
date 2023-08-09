package com.weit.data.di.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weit.data.model.Coordinate

@Database(
    entities = [Coordinate::class],
    version = 1,
    exportSchema = false,
)
abstract class CoordinateDatabase : RoomDatabase() {
    abstract fun coordinateDao(): CoordinateDao
}
