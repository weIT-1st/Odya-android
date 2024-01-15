package com.weit.domain.usecase.user

import com.weit.domain.model.user.UserStatistics
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserStatisticsUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userId: Long): Result<UserStatistics> =
        repository.getUserStatistics(userId)
}
