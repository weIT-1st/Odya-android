package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetIsLocationInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(isLocationInfo: Boolean): Result<Unit> =
        userInfoRepository.setIsLocationInfo(isLocationInfo)
}
