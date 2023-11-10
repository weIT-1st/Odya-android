package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class GetNicknameUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(): Result<String?> =
        userInfoRepository.getNickname()
}
