package com.weit.domain.usecase.user

import com.weit.domain.model.user.User
import com.weit.domain.repository.user.UserRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UpdateProfileUseCase@Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend fun updateProfile(file: MultipartBody.Part): Result<Unit> =
        userRepository.updateProfile(file)
}
