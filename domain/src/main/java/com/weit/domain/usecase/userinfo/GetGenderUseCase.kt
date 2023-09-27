package com.weit.domain.usecase.userinfo

import com.weit.domain.model.GenderType
import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class GetGenderUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(): GenderType {
        val result = userInfoRepository.getGender()
        val genderType = result.getOrThrow()

        return if (result.isSuccess) {
            GenderType.values().find { it.type == genderType } ?: GenderType.IDLE
        } else {
            GenderType.IDLE
        }
    }
}
