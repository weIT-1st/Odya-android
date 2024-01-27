package com.weit.domain.usecase.userinfo

import com.weit.domain.repository.userinfo.UserInfoRepository
import javax.inject.Inject

class SetIsAlarmCommentUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
) {
    suspend operator fun invoke(isAlarmComment: Boolean): Result<Unit> =
        userInfoRepository.setIsAlarmComment(isAlarmComment)
}
