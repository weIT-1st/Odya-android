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

    private val _currentLatLng = MutableStateFlow(DEFAULT_LAT_LNG)
    val currentLatLng: StateFlow<LatLng> get() = _currentLatLng

    private val allOdyaList = MutableStateFlow<List<CoordinateUserImageResponseInfo>>(emptyList())

    private val _odyaList = MutableStateFlow<List<CoordinateUserImageResponseInfo>>(emptyList())
    val odyaList : StateFlow<List<CoordinateUserImageResponseInfo>> get() = _odyaList

    val isListOnlyMy = MutableStateFlow<Boolean>(true)

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getCurrentPlace()
        getTopic()
    }

    fun getCurrentPlace() {
        viewModelScope.launch {
            val current = getCurrentPlaceUseCase()

            if (current.isSuccess) {
                val result = current.getOrThrow()
                _currentLatLng.emit(DEFAULT_LAT_LNG)
                _currentLatLng.emit(LatLng(result.lat.toDouble(), result.lng.toDouble()))
            } else {
                // todo 에러처리
            }
        }
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
            val rightUp = ImageDoubleLatLng(northeast.latitude, northeast.longitude)
            val leftDown = ImageDoubleLatLng(southwest.latitude, southwest.longitude)
            val result = getCoordinateUserImageUseCase(
                rightUp,
                leftDown
            )

            if (result.isSuccess) {
                val list = result.getOrThrow()
                allOdyaList.emit(list)
                setOdyaByIsListOnlyMy()
            } else {
                Log.d("getCoordinateUserImage", "fail : ${result.exceptionOrNull()}")
            }
        }

    }

    fun setOdyaByIsListOnlyMy(){
        viewModelScope.launch{
            val list = allOdyaList.value
            val isChecked = isListOnlyMy.value

            if (isChecked){
                _odyaList.emit(list)
            } else {
                _odyaList.emit(list.filter { it.imageUserType == ImageUserType.USER })
            }
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

    fun popUpSearchPlace(placeId: String?) {
        viewModelScope.launch {
            _event.emit(Event.PopUpSearchPlace(placeId))
        }
    }

    fun popUpMainSearch() {
        viewModelScope.launch{
            _event.emit(Event.PopUpMainSearch)
        }
    }

    sealed class Event {
        data class PopUpSearchPlace(
            val placeId: String?
        ) : Event()
        object PopUpMainSearch: Event()
        object FirstLogin : Event()
    }

    companion object {
        // 서울역
        private val DEFAULT_LAT_LNG = LatLng(37.55476719052827, 126.97082417355988)
    }
}
