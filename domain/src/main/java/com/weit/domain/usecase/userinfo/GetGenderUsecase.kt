package com.weit.domain.usecase.userinfo

import com.weit.domain.model.GenderType
import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class GetGenderUsecase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(): GenderType {
        val result = userInfoRepository.getGender()

        return if (result.isSuccess) {
            GenderType.values().find { it.type == result.getOrThrow() } ?: GenderType.IDLE
        } else {
            GenderType.IDLE
        }
    }
}
