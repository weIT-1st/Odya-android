package com.weit.domain.usecase.image

import com.weit.domain.model.image.ImageLatLng
import com.weit.domain.repository.image.ImageRepository
import javax.inject.Inject

class GetCoordinatesUseCase @Inject constructor(
    private val repository: ImageRepository,
) {
    suspend operator fun invoke(uri : String?): ImageLatLng =
        repository.getCoordinates(uri)

}
