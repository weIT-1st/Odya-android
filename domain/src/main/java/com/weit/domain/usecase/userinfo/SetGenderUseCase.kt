package com.weit.domain.usecase.userinfo

import com.weit.domain.model.GenderType
import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetGenderUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(genderType: GenderType): Result<Unit> =
        userInfoRepository.setGender(genderType.type)
}

