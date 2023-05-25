package com.weit.domain.repository.example

import com.weit.domain.model.WeitUser

interface ExampleRepository {
    suspend fun getUser(name: String): Result<WeitUser>
}
