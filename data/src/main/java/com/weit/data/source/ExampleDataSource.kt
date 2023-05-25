package com.weit.data.source

import com.weit.data.model.WeitUserDTO
import com.weit.data.service.ExampleService
import javax.inject.Inject

class ExampleDataSource @Inject constructor(
    private val service: ExampleService,
) {
    suspend fun getUser(name: String): WeitUserDTO =
        service.getUser(name)
}
