package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.login.UserInfoRepository
import javax.inject.Inject

class SetNicknameUsecase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) {
    suspend operator fun invoke(nickname: String) =
        userInfoRepository.setNickname(nickname)
}