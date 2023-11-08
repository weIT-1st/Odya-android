package com.weit.presentation.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weit.domain.model.journal.TravelJournalContentsImagesInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.journal.TravelJournalPlaceList
import com.weit.domain.model.place.PlaceDetail
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.journal.GetFriendTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetFriendTravelJournalPlaceListUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalPlaceListUseCase
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
    val getPlacesByCoordinateUseCase: GetPlacesByCoordinateUseCase,
    val getCurrentPlaceUseCase: GetCurrentPlaceUseCase,
    val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    val getMyTravelJournalPlaceListUseCase: GetMyTravelJournalPlaceListUseCase,
    val getFriendTravelJournalPlaceListUseCase: GetFriendTravelJournalPlaceListUseCase
) : ViewModel() {

    private val _touchPlaceId = MutableStateFlow("")
    val touchPlaceId: StateFlow<String> get() = _touchPlaceId

    private val _detailPlace = MutableEventFlow<PlaceDetail>()
    val detailPlace = _detailPlace.asEventFlow()

    private val _currentLatLng = MutableStateFlow<LatLng>(DEFAULT_LAT_LNG)
    val currentLatLng : StateFlow<LatLng> get() = _currentLatLng

    private val _myOdyaList = MutableStateFlow<List<TravelJournalPlaceList>>(emptyList())
    val myOdyaList : StateFlow<List<TravelJournalPlaceList>> get() = _myOdyaList

    private val _friendOdyaList = MutableStateFlow<List<TravelJournalPlaceList>>(emptyList())
    val friendOdyaList : StateFlow<List<TravelJournalPlaceList>> get() = _friendOdyaList

    private val _friendWithMyOdyaList = MutableStateFlow<List<TravelJournalPlaceList>>(emptyList())
    val friendWithMyOdyaList : StateFlow<List<TravelJournalPlaceList>> get() = _friendOdyaList

    init {
        viewModelScope.launch {
            val current = getCurrentPlaceUseCase()
            if (current.isSuccess){
                val result = current.getOrThrow()
                _currentLatLng.emit(LatLng(result.lat.toDouble(), result.lng.toDouble()))
            }

            getMyOdyaList()
            getFriendOdyaList()
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

    fun getDetailPlace(placeId: String) {
        viewModelScope.launch {
            val result = getPlaceDetailUseCase(placeId)
            _detailPlace.emit(result)
        }
    }

    private suspend fun getMyOdyaList(){
        val result = getMyTravelJournalPlaceListUseCase(null, null)
        _myOdyaList.emit(result)
    }

    private suspend fun getFriendOdyaList(){
        val result = getFriendTravelJournalPlaceListUseCase(null, null)
        _myOdyaList.emit(result)
    }

    private suspend fun getFriendWithMyOdyaList(){
        val myOdyaList = myOdyaList.value
        val friendOdyaList = friendOdyaList.value
        val newFriendWithMyOdyaList = emptyList<List<TravelJournalPlaceList>>().toMutableList()

        newFriendWithMyOdyaList.add(myOdyaList)
        newFriendWithMyOdyaList.add(friendOdyaList)

        _friendWithMyOdyaList.emit(friendOdyaList)
    }


    companion object{
        // 서울역
        private val DEFAULT_LAT_LNG = LatLng(37.55476719052827, 126.97082417355988)
    }
}
