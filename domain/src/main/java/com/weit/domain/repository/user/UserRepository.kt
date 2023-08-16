package com.weit.domain.repository.user

import com.weit.domain.model.user.User

interface UserRepository {

    suspend fun getUser(): Result<User>

    suspend fun updateEmail(emailUpdateUser: User): Result<Unit>

    suspend fun updatePhoneNumber(phoneNumberUpdateUser: User): Result<Unit>

    suspend fun updateInformation(informationUpdateUser: User): Result<Unit>

    suspend fun updateProfile(uri: String): Result<Unit>

    suspend fun setUserId(userId: Long)

    suspend fun getUserId(): Long
}
