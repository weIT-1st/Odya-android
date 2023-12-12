package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetTermIdListUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(termIdList: Set<String>): Result<Unit> =
        userInfoRepository.setTermIdList(termIdList)
}
