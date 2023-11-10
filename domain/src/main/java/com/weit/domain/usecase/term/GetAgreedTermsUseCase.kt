package com.weit.domain.usecase.term

import com.weit.domain.model.auth.AgreedTermInfo
import com.weit.domain.repository.term.TermRepository
import javax.inject.Inject

class GetAgreedTermsUseCase @Inject constructor(
    private val repository: TermRepository,
) {
    suspend operator fun invoke(): Result<List<AgreedTermInfo>> =
        repository.getAgreedTerms()
}
