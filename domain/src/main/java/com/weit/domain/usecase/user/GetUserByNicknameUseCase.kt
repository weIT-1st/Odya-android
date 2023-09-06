package com.weit.domain.usecase.user

import com.weit.domain.model.user.User
import com.weit.domain.model.user.UserByNicknameInfo
import com.weit.domain.model.user.UserContent
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserByNicknameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userByNicknameInfo: UserByNicknameInfo): Result<List<UserContent>> =
        userRepository.getUserByNickname(userByNicknameInfo)
}
