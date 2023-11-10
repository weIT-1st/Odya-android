package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import java.time.LocalDate
import javax.inject.Inject

class SetBirthUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(birth: LocalDate): Result<Unit> =
        userInfoRepository.setBirth(birth)
}
