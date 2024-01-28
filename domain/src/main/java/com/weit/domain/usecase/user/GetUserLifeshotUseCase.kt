package com.weit.domain.usecase.user

import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.model.user.LifeshotRequestInfo
import com.weit.domain.model.user.UserStatistics
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserLifeshotUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(lifeshotRequestInfo: LifeshotRequestInfo): Result<List<UserImageResponseInfo>> =
        repository.getUserLifeShot(lifeshotRequestInfo)
}
