package com.weit.presentation.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetPlacesByCoordinateUseCase
import com.weit.domain.usecase.place.GetSearchPlaceUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val getSearchPlaceUseCase: GetSearchPlaceUseCase,
    val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    val getPlacesByCoordinateUseCase: GetPlacesByCoordinateUseCase,
) : ViewModel() {

    private val _searchPlaceList = MutableStateFlow<List<PlacePrediction>>(emptyList())
    val searchPlaceList: StateFlow<List<PlacePrediction>> get() = _searchPlaceList

    private val _touchPlaceId = MutableStateFlow("")
    val touchPlaceId: StateFlow<String> get() = _touchPlaceId

    private val _detailPlace = MutableEventFlow<PlaceDetail>()
    val detailPlace = _detailPlace.asEventFlow()
    fun searchPlace(query: String) {
        viewModelScope.launch {
            val result = getSearchPlaceUseCase(query)
            _searchPlaceList.emit(result)
        }
    }

    fun getDetailPlace(placeId: String) {
        viewModelScope.launch {
            val result = getPlaceDetailUseCase(placeId)
            _detailPlace.emit(result)
            result?.placeId?.let { _touchPlaceId.emit(it) }
        }
    }

    fun getPlaceByCoordinate(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val place = getPlacesByCoordinateUseCase(latitude, longitude)

            Log.d("getPlaceImage", "item count : ${place.size}")
            for (item in place) {
                Log.d("getPlaceImage", "placeId : ${item.placeId}")
                Log.d("getPlaceImage", "name : ${item.name}")
                Log.d("getPlaceImage", "address : ${item.address}")
            }

            if (place.isEmpty().not()) {
                _touchPlaceId.emit(place[0].placeId)
                Log.d("getPlaceImage", "touchPlaceId : ${touchPlaceId.value}")
            } else {
                Log.d("getPlaceImage", "notPlaceId")
            }
        }
    }
}
