package com.weit.data.source

import com.weit.data.model.auth.term.AgreedTermListDTO
import com.weit.data.model.auth.term.TermIdList
import com.weit.data.service.TermService
import retrofit2.Response
import javax.inject.Inject

class TermDataSource @Inject constructor(
    private val service: TermService,
) {

    suspend fun getAgreedTerms(): AgreedTermListDTO =
        service.getAgreedTerms()

    suspend fun setTerms(termsIdList: TermIdList): Response<Unit> {
        return service.setTerms(termsIdList)
    }
}
