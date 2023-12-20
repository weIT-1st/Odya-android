package com.weit.domain.repository.user

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.CoordinateTimeInfo
import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.model.user.search.UserSearchInfo

interface UserSearchRepository {

    suspend fun insertUser(user: UserSearchInfo)

    suspend fun deleteUser(Id: Long)

    suspend fun getUser(): List<UserSearchInfo>

}
