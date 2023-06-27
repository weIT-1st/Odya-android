package com.weit.domain.repository.user

import com.weit.domain.model.user.User

interface UserRepository {

    suspend fun getUser(): Result<User>

    suspend fun updateEmail(email : String): Result<Unit>

    suspend fun updatePhoneNumber(phonenumber: String) : Result<Unit>

    suspend fun updateInformation(nickname : String) : Result<Unit>
}