package com.weit.presentation.ui.post.selectplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetSearchPlaceUseCase
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SelectPlaceViewModel @AssistedInject constructor(
    @Assisted imagePlacesDTO: List<PlacePredictionDTO>,
    private val getSearchPlaceUseCase: GetSearchPlaceUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val imagePlaces = imagePlacesDTO.toPlacePredictions()

    private val _places = MutableStateFlow(imagePlaces)
    val places: StateFlow<List<PlacePrediction>> get() = _places

    val query = MutableStateFlow("")

    private var searchJob: Job = Job().apply {
        complete()
    }

    @AssistedFactory
    interface SelectPlaceFactory {
        fun create(imagePlaces: List<PlacePredictionDTO>): SelectPlaceViewModel
    }

    fun onClickPointOfInterest(pointOfInterest: PointOfInterest) {
        viewModelScope.launch {
            _event.emit(Event.SetMarker(pointOfInterest.latLng))
        }
    }

    fun onClickSearchedPlace(place: PlacePrediction) {
        viewModelScope.launch {
            val placeDetail = getPlaceDetailUseCase(place.placeId)
            val latitude = placeDetail.latitude ?: return@launch
            val longitude = placeDetail.longitude ?: return@launch
            val latlng = LatLng(latitude, longitude)
            _event.emit(Event.SetMarker(latlng))
            _event.emit(Event.MoveMap(latlng))
        }
    }

    fun onSearch(query: String) {
        searchJob.cancel()
        searchPlace(query)
    }

    private fun searchPlace(query: String) {
        searchJob = viewModelScope.launch {
            val searchedPlaces = if (query.isBlank()) {
                imagePlaces.toList()
            } else {
                getSearchPlaceUseCase(query)
            }
            _places.emit(searchedPlaces)
        }
    }

    private fun List<PlacePredictionDTO>.toPlacePredictions() = map {
        PlacePrediction(it.placeId, it.name, it.address)
    }

    sealed class Event {
        data class SetMarker(val latLng: LatLng) : Event()
        data class MoveMap(val latLng: LatLng) : Event()
    }

    companion object {
        fun create(
            viewModelFactory: SelectPlaceFactory,
            imagePlaces: List<PlacePredictionDTO>,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return viewModelFactory.create(imagePlaces) as T
                }
            }
        }
    }
}
