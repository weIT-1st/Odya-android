package com.weit.presentation.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.place.GetCurrentPlaceUseCase
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
    val getCurrentPlaceUseCase: GetCurrentPlaceUseCase
) : ViewModel() {

    private val _searchPlaceList = MutableStateFlow<List<PlacePrediction>>(emptyList())
    val searchPlaceList: StateFlow<List<PlacePrediction>> get() = _searchPlaceList

    private val _touchPlaceId = MutableStateFlow("")
    val touchPlaceId: StateFlow<String> get() = _touchPlaceId

    private val _detailPlace = MutableEventFlow<PlaceDetail>()
    val detailPlace = _detailPlace.asEventFlow()

    private val _currentLatLng = MutableStateFlow<LatLng>(DEFAULT_LAT_LNG)
    val currentLatLng : StateFlow<LatLng> get() = _currentLatLng

    init {
        viewModelScope.launch {
            val current = getCurrentPlaceUseCase()
            if (current.isSuccess){
                val result = current.getOrThrow()
                _currentLatLng.emit(LatLng(result.lat.toDouble(), result.lng.toDouble()))
            }
        }
    }
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
            if (result.placeId.isNullOrBlank().not()){
                _touchPlaceId.emit(result.placeId!!)
            }
        }
    }

    fun getPlaceByCoordinate(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = getPlacesByCoordinateUseCase(latitude, longitude)

            if (result.isSuccess){
                val place = result.getOrThrow()
                _touchPlaceId.emit(place[0].placeId)
            }
        }
    }

    companion object{
        // 서울역
        private val DEFAULT_LAT_LNG = LatLng(37.55476719052827, 126.97082417355988)
    }
}
