package com.weit.domain.repository.term

import com.weit.domain.model.auth.AgreedTermInfo
import com.weit.domain.model.auth.TermIdListInfo

interface TermRepository {
    suspend fun getAgreedTerms(): Result<List<AgreedTermInfo>>
    suspend fun setTerms(termIdListInfo: TermIdListInfo): Result<Unit>
}
