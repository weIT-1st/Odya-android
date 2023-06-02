package com.weit.domain.usecase.example

import com.weit.domain.model.WeitUser
import com.weit.domain.repository.example.ExampleRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: ExampleRepository,
) {
    suspend operator fun invoke(name: String): Result<WeitUser> =
        repository.getUser(name)
}
