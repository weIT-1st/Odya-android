package com.weit.domain.usecase.user

import com.weit.domain.model.user.SearchUserContent
import com.weit.domain.model.user.SearchUserRequestInfo
import com.weit.domain.repository.user.UserRepository
import javax.inject.Inject

class SearchUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(searchUserRequestInfo: SearchUserRequestInfo): Result<List<SearchUserContent>> =
        repository.searchUser(searchUserRequestInfo)
}
