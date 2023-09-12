package com.weit.domain.usecase.term

import com.weit.domain.model.auth.TermContentInfo
import com.weit.domain.repository.auth.AuthRepository
import javax.inject.Inject

class GetTermContentUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(termId: Long): Result<TermContentInfo> =
        repository.getTermContent(termId)
}
