package com.weit.data.source

import com.weit.data.db.CoordinateDatabase
import com.weit.data.db.UserSearchDatabase
import com.weit.data.model.coordinate.Coordinate
import com.weit.data.model.user.search.UserSearch
import com.weit.domain.model.CoordinateTimeInfo
import javax.inject.Inject

class UserSearchDataSource @Inject constructor(
    private val db: UserSearchDatabase,
) {
    suspend fun insertUser(userSearch: UserSearch) {
        db.userDao().insertUser(userSearch)
    }

    suspend fun deleteUser(userSearchId: Long) {
        db.userDao().deleteUser(userSearchId)
    }

    suspend fun getUsers(): List<UserSearch> {
        return db.userDao().getUsers()
    }
}
