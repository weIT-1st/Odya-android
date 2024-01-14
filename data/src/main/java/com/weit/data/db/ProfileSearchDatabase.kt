package com.weit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weit.data.model.user.search.UserSearch

@Database(
    entities = [UserSearch::class],
    version = 1,
    exportSchema = false,
)
abstract class ProfileSearchDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
