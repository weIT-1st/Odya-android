package com.weit.presentation.ui.post.selectplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.weit.domain.model.place.PlacePrediction
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SelectPlaceViewModel @AssistedInject constructor(
    @Assisted imagePlacesDTO: List<PlacePredictionDTO>,
) : ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val imagePlaces = imagePlacesDTO.toPlacePredictions()

    private val _places = MutableStateFlow(imagePlaces)
    val places: StateFlow<List<PlacePrediction>> get() = _places

    val query = MutableStateFlow("")

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

    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _places.emit(imagePlaces.toList())
            } else {
                // 검색 결과
            }
        }
    }

    private fun List<PlacePredictionDTO>.toPlacePredictions() = map {
        PlacePrediction(it.placeId, it.name, it.address)
    }

    sealed class Event {
        data class SetMarker(val latLng: LatLng) : Event()
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
