package com.weit.domain.usecase.user

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import com.weit.domain.repository.user.UserSearchRepository
import javax.inject.Inject

class DeleteUserSearchUseCase @Inject constructor(
    private val repository: UserSearchRepository,
) {
    suspend operator fun invoke(userSearchId: Long) =
        repository.deleteUser(userSearchId)
}
