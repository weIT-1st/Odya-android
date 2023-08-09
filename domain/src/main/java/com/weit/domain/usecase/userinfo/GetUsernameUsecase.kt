package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.login.UserInfoRepository
import javax.inject.Inject

class GetUsernameUsecase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) {
    suspend operator fun invoke(): Result<String?> =
        userInfoRepository.getUsername()
}