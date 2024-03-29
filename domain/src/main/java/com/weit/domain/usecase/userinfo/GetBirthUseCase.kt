package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import java.time.LocalDate
import javax.inject.Inject

class GetBirthUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(): Result<LocalDate?> =
        userInfoRepository.getBirth()
}
