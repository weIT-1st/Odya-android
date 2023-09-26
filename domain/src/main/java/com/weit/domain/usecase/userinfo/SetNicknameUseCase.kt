package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetNicknameUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(nickname: String) =
        userInfoRepository.setNickname(nickname)
}
