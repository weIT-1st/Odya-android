package com.weit.domain.usecase.userinfo

import com.weit.domain.model.GenderType
import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetGenderUsecase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) {
    suspend operator fun invoke(genderType: GenderType)  {
        var gender = "IDLE"
        if (genderType.equals(GenderType.FEMALE)){
            gender = "FEMALE"
        } else if (genderType.equals(GenderType.MALE)){
            gender = "MALE"
        }
        userInfoRepository.setGender(gender)
    }
}
