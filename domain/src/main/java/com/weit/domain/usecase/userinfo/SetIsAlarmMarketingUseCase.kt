package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetIsAlarmMarketingUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(isAlarmMarketing: Boolean): Result<Unit> =
        userInfoRepository.setIsAlarmMarketing(isAlarmMarketing)
}
