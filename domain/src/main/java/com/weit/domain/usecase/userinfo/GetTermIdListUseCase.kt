package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class GetTermIdListUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(): Set<Long> {
        val result = userInfoRepository.getTermIdList()
        return result.getOrThrow()?.mapNotNull {
            it.toLongOrNull()
        }?.toSet() ?: emptySet()
    }
}
