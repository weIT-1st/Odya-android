package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetIsAlarmLikeUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(isAlarmLike: Boolean): Result<Unit> =
        userInfoRepository.setIsAlarmLike(isAlarmLike)
}
