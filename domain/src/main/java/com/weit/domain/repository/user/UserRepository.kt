package com.weit.domain.repository.user

import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserStatistics

interface UserRepository {

    suspend fun getUser(): Result<User>

    suspend fun updateEmail(emailUpdateUser: User): Result<Unit>

    suspend fun updatePhoneNumber(phoneNumberUpdateUser: User): Result<Unit>

    suspend fun updateInformation(informationUpdateUser: User): Result<Unit>

    suspend fun updateProfile(uri: String?): Result<Unit>

    suspend fun setUserId(userId: Long)

    suspend fun getUserId(): Long

    suspend fun deleteUser() : Result<Unit>

    suspend fun searchUser(searchUserRequestInfo: SearchUserRequestInfo): Result<List<SearchUserContent>>

    suspend fun getUserStatistics(userId: Long): Result<UserStatistics>


}
