package com.weit.domain.usecase.user

import com.weit.domain.model.CoordinateInfo
import com.weit.domain.model.user.search.UserSearchInfo
import com.weit.domain.repository.coordinate.CoordinateRepository
import com.weit.domain.repository.user.UserSearchRepository
import javax.inject.Inject

class InsertUserSearchUseCase @Inject constructor(
    private val repository: UserSearchRepository,
) {
    suspend operator fun invoke(userSearchInfo: UserSearchInfo) =
        repository.insertUser(userSearchInfo)
}
