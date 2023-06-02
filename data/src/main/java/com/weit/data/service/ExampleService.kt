package com.weit.data.service

import com.weit.data.model.WeitUserDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface ExampleService {
    @POST("test")
    suspend fun getUser(
        @Body name: String,
    ): WeitUserDTO
}
