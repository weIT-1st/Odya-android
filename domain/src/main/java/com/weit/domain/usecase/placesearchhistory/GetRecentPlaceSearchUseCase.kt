package com.weit.domain.usecase.placesearchhistory

import com.weit.domain.repository.place.PlaceSearchHistoryRepository
import javax.inject.Inject

class GetRecentPlaceSearchUseCase @Inject constructor(
    private val placeSearchHistoryRepository: PlaceSearchHistoryRepository
){
    suspend operator fun invoke(): Result<List<String>> =
        placeSearchHistoryRepository.getRecentPlaceSearch()

}
