package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetIsAlarmAllUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(isAlarmAll: Boolean): Result<Unit> =
        userInfoRepository.setIsAlarmAll(isAlarmAll)
}
