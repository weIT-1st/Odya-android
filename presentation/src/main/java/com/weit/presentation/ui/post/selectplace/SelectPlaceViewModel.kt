package com.weit.presentation.ui.post.selectplace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.coordinate.GetStoredCoordinatesUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetSearchPlaceUseCase
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.model.post.place.SelectPlaceDTO
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
    @Assisted private val dailyTravelLogPosition: Int,
    private val getSearchPlaceUseCase: GetSearchPlaceUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
) : ViewModel() {

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private val imagePlaces = imagePlacesDTO.toPlacePredictions()

    private var searchedPlaces = listOf<PlacePrediction>()

    private val _placeEntities = MutableStateFlow(imagePlaces.toSelectPlaceEntity())
    val placeEntities: StateFlow<List<SelectPlaceEntity>> get() = _placeEntities

    private val _currentAddress = MutableStateFlow("")
    val currentAddress: StateFlow<String> get() = _currentAddress

    val query = MutableStateFlow("")

    private var selectedPlaceEntity: SelectPlaceEntity? = null

    private var searchJob: Job = Job().apply {
        complete()
    }

    @AssistedFactory
    interface SelectPlaceFactory {
        fun create(imagePlaces: List<PlacePredictionDTO>, dailyTravelLogPosition: Int): SelectPlaceViewModel
    }

    fun onClickPointOfInterest(pointOfInterest: PointOfInterest) {
        viewModelScope.launch {
            _event.emit(Event.SetMarker(pointOfInterest.latLng))
            val placeDetail = getPlaceDetailUseCase(pointOfInterest.placeId)
            setSelectedPlaceEntity(placeDetail.toPlacePrediction())
            updateSelectPlaceEntities(searchedPlaces.toSelectPlaceEntity())
            _currentAddress.emit(selectedPlaceEntity?.place?.address ?: "")
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
            setSelectedPlaceEntity(placeDetail.toPlacePrediction())
            updateSelectPlaceEntities(searchedPlaces.toSelectPlaceEntity())
            _currentAddress.emit(selectedPlaceEntity?.place?.address ?: "")
        }
    }

    fun onSearch(query: String) {
        searchJob.cancel()
        searchPlace(query)
    }

    fun onComplete() {
        val entity = selectedPlaceEntity ?: return
        val selectPlaceDTO = SelectPlaceDTO(
            placeId = entity.place.placeId,
            name = entity.place.name,
            address = entity.place.address,
            position = dailyTravelLogPosition,
        )
        viewModelScope.launch {
            _event.emit(Event.OnComplete(selectPlaceDTO))
        }
    }

    private fun updateSelectPlaceEntities(currentEntities: List<SelectPlaceEntity>) {
        viewModelScope.launch {
            selectedPlaceEntity?.let { selectedEntity ->
                val result = listOf(selectedEntity)
                    .plus(
                        currentEntities.filterNot {
                            it.place.placeId == selectedEntity.place.placeId || it.isSelected
                        },
                    )
                _placeEntities.emit(result)
            } ?: _placeEntities.emit(currentEntities)
        }
    }

    private fun setSelectedPlaceEntity(place: PlacePrediction) {
        // 두 번 선택은 취소로 간주
        selectedPlaceEntity = if (selectedPlaceEntity?.place?.placeId == place.placeId) {
            null
        } else {
            SelectPlaceEntity(place, true)
        }
    }

    private fun searchPlace(query: String) {
        searchJob = viewModelScope.launch {
            searchedPlaces = if (query.isBlank()) {
                imagePlaces.toList()
            } else {
                getSearchPlaceUseCase(query)
            }
            updateSelectPlaceEntities(searchedPlaces.toSelectPlaceEntity())
        }
    }

    private fun List<PlacePredictionDTO>.toPlacePredictions() = map {
        PlacePrediction(it.placeId, it.name, it.address)
    }

    private fun List<PlacePrediction>.toSelectPlaceEntity() = map {
        SelectPlaceEntity(it, false)
    }

    private fun PlaceDetail.toPlacePrediction() = PlacePrediction(
        placeId = placeId,
        name = name ?: "",
        address = address ?: "",
    )

    sealed class Event {
        data class SetMarker(val latLng: LatLng) : Event()
        data class MoveMap(val latLng: LatLng) : Event()
        data class OnComplete(val dto: SelectPlaceDTO) : Event()
    }

    companion object {
        fun create(
            viewModelFactory: SelectPlaceFactory,
            imagePlaces: List<PlacePredictionDTO>,
            dailyTravelLogPosition: Int,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return viewModelFactory.create(imagePlaces, dailyTravelLogPosition) as T
                }
            }
        }
    }
}
