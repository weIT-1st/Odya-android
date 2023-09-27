package com.weit.data.service

import com.weit.data.model.auth.term.AgreedTermListDTO
import com.weit.data.model.auth.term.TermIdList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface TermService {

    @GET("/api/v1/terms")
    suspend fun getAgreedTerms(): AgreedTermListDTO

    @PATCH("/api/v1/terms")
    suspend fun setTerms(
        @Body termsIdList: TermIdList,
    ): Response<Unit>
}
