package com.weit.domain.usecase.term

import com.weit.domain.model.auth.TermInfo
import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GetTermListUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<List<TermInfo>> =
        repository.getTermList()
}
