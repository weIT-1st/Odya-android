package com.weit.data.di.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weit.data.model.Location


@Database(
    entities = [Location::class],
    version = 1,
    exportSchema = false
)

abstract class LocationDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}