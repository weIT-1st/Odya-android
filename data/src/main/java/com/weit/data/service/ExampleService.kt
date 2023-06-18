package com.weit.data.service

import com.weit.data.model.WeitUserDTO
import com.weit.data.model.WeitUserName
import retrofit2.http.Body
import retrofit2.http.POST

interface ExampleService {

    @POST("test")
    suspend fun getUser(
        @Body testRequest: WeitUserName,
    ): WeitUserDTO
}
