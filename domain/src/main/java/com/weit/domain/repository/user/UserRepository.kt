package com.weit.domain.repository.user

import com.weit.domain.model.user.User
import okhttp3.MultipartBody

interface UserRepository {

    suspend fun getUser(): Result<User>

    suspend fun updateEmail(emailUpdateUser: User): Result<Unit>

    suspend fun updatePhoneNumber(phoneNumberUpdateUser: User): Result<Unit>

    suspend fun updateInformation(informationUpdateUser: User): Result<Unit>

    suspend fun updateProfile(file: MultipartBody.Part): Result<Unit>
}
