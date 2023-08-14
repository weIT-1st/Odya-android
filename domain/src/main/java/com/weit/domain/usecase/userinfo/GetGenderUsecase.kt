package com.weit.domain.usecase.userinfo

import com.weit.domain.model.GenderType
import com.weit.domain.repository.userinfo.UserInfoRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class GetGenderUsecase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
){
    suspend operator fun invoke(): GenderType {
        var genderType = GenderType.IDLE

        val gottenGender = userInfoRepository.getGender()
        if (gottenGender.equals("MALE")) {
            genderType = GenderType.MALE
        } else if (gottenGender.equals("FEMALE")) {
            genderType = GenderType.FEMALE
        } else {
            genderType = GenderType.IDLE
        }

        return genderType
    }
}
