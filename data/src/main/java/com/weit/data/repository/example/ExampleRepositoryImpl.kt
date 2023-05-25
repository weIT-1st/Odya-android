package com.weit.data.repository.example

import com.weit.data.model.WeitUserDTO
import com.weit.data.source.ExampleDataSource
import com.weit.domain.model.WeitUser
import com.weit.domain.repository.example.ExampleRepository
import javax.inject.Inject

class ExampleRepositoryImpl @Inject constructor(
    private val dataSource: ExampleDataSource,
) : ExampleRepository {
    override suspend fun getUser(name: String): Result<WeitUser> =
        runCatching {
            dataSource.getUser(name).toWeitUser()
        }

    private fun WeitUserDTO.toWeitUser() =
        WeitUser(
            hash = hash,
            name = name,
        )
}
