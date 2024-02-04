package com.weit.presentation.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.domain.model.image.ImageDoubleLatLng
import com.weit.domain.model.image.ImageLatLng
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.user.ImageUserType
import com.weit.domain.usecase.image.GetCoordinateUserImageUseCase
import com.weit.domain.usecase.place.GetCurrentPlaceUseCase
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetPlacesByCoordinateUseCase
import com.weit.domain.usecase.topic.GetFavoriteTopicListUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val getPlacesByCoordinateUseCase: GetPlacesByCoordinateUseCase,
    val getCurrentPlaceUseCase: GetCurrentPlaceUseCase,
    val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    val getCoordinateUserImageUseCase: GetCoordinateUserImageUseCase,
    val getFavoriteTopicListUseCase: GetFavoriteTopicListUseCase
) : ViewModel() {

    private val _touchPlaceId = MutableStateFlow("")
    val touchPlaceId: StateFlow<String> get() = _touchPlaceId

    private val _detailPlace = MutableEventFlow<PlaceDetail>()
    val detailPlace = _detailPlace.asEventFlow()

    private val _currentLatLng = MutableStateFlow<LatLng>(DEFAULT_LAT_LNG)
    val currentLatLng: StateFlow<LatLng> get() = _currentLatLng

    private val _odyaList = MutableStateFlow<List<CoordinateUserImageResponseInfo>>(emptyList())
    val odyaList: StateFlow<List<CoordinateUserImageResponseInfo>> get() = _odyaList

    private val _odyaAllList = MutableStateFlow<List<CoordinateUserImageResponseInfo>>(emptyList())
    val odyaAllList: StateFlow<List<CoordinateUserImageResponseInfo>> get() = _odyaAllList

    private val _odyaToggle = MutableStateFlow<Boolean>(false)
    val odyaToggle: StateFlow<Boolean> get() = _odyaToggle
    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()
    init {
        viewModelScope.launch {
            val current = getCurrentPlaceUseCase()
            if (current.isSuccess) {
                val result = current.getOrThrow()
                _currentLatLng.emit(LatLng(result.lat.toDouble(), result.lng.toDouble()))
            }
        }
        getTopic()
    }

    fun getPlaceByCoordinate(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = getPlacesByCoordinateUseCase(latitude, longitude)

            if (result.isSuccess) {
                val place = result.getOrThrow()
                _touchPlaceId.emit(place[0].placeId)
            }
        }
    }

    fun getDetailPlace(placeId: String) {
        viewModelScope.launch {
            val result = getPlaceDetailUseCase(placeId)
            _detailPlace.emit(result)
        }
    }


    fun getOdyaList(northeast: LatLng, southwest: LatLng ) {
        viewModelScope.launch {
            val toggleOdyaOnOff = odyaToggle.value
            val rightUp = ImageDoubleLatLng(northeast.latitude, northeast.longitude)
            val leftDown = ImageDoubleLatLng(southwest.latitude, southwest.longitude)
            val result = getCoordinateUserImageUseCase(
                rightUp,
                leftDown
            )

            if (result.isSuccess) {
                val list = result.getOrThrow()
                _odyaAllList.emit(list)
                setOdyaByToggleOnOff(toggleOdyaOnOff, list)

            } else {
                Log.d("getCoordinateUserImage", "fail : ${result.exceptionOrNull()}")
            }
        }

    }

    fun changeOdyaToggleOff(isChecked: Boolean) {
        viewModelScope.launch {
            _odyaToggle.emit(isChecked)
            val list = odyaAllList.value

            setOdyaByToggleOnOff(isChecked, list)
        }
    }

    private suspend fun setOdyaByToggleOnOff(isChecked: Boolean, list: List<CoordinateUserImageResponseInfo>){
        if (isChecked){
            _odyaList.emit(list.filter { it.imageUserType == ImageUserType.USER })
        } else {
            _odyaList.emit(list.filterNot { it.imageUserType == ImageUserType.OTHER })
        }
    }

    private fun getTopic() {
        viewModelScope.launch {
            val result = getFavoriteTopicListUseCase()
            if (result.isSuccess) {
                val list = result.getOrThrow()

                if (list.isEmpty()) {
                    _event.emit(Event.FirstLogin)
                }
            } else {
                Log.d("getFavoriteTopic", "Get Favorite Topic Fail : ${result.exceptionOrNull()}")
            }
        }
    }

    sealed class Event {
        object FirstLogin : Event()
    }

    companion object {
        // 서울역
        private val DEFAULT_LAT_LNG = LatLng(37.55476719052827, 126.97082417355988)
    }
}
