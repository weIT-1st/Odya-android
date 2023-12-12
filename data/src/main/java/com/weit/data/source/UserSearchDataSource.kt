package com.weit.data.source

import com.weit.data.db.ProfileSearchDatabase
import com.weit.data.model.user.search.UserSearch
import javax.inject.Inject

class UserSearchDataSource @Inject constructor(
    private val db: ProfileSearchDatabase,
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
