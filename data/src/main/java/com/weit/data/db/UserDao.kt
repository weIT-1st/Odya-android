package com.weit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weit.data.model.user.search.UserSearch

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userSearch: UserSearch)

    @Query("DELETE FROM usersearch WHERE userId=:userId")
    suspend fun deleteUser(userId: Long)

    @Query("SELECT * FROM usersearch ORDER BY time DESC")
    suspend fun getUsers(): List<UserSearch>
}
