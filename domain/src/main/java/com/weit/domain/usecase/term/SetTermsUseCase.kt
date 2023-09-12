package com.weit.domain.usecase.term

import com.weit.domain.model.auth.TermIdListInfo
import com.weit.domain.repository.term.TermRepository
import javax.inject.Inject

class SetTermsUseCase @Inject constructor(
    private val repository: TermRepository,
) {
    suspend operator fun invoke(termIdListInfo: TermIdListInfo): Result<Unit> =
        repository.setTerms(termIdListInfo)
}
