package com.weit.domain.usecase.image

import com.weit.domain.model.image.UserImageResponseInfo
import com.weit.domain.repository.image.ImageAPIRepository
import javax.inject.Inject

class GetUserImageUseCase @Inject constructor(
    private val repository: ImageAPIRepository
) {
    suspend operator fun invoke(size: Int?, lastId: Long?): Result<List<UserImageResponseInfo>> =
        repository.getUserImage(size, lastId)
}
