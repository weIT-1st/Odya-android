package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.login.UserInfoRepository
import javax.inject.Inject

class GetNicknameUsecase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) {
    suspend operator fun invoke(): Result<String?> =
        userInfoRepository.getNickname()
}