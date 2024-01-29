package com.weit.presentation.ui.journal.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.place.PlaceDetail
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TravelJournalMapViewModel @AssistedInject constructor(
    @Assisted private val travelJournalInfo: TravelJournalInfo
) : ViewModel() {

    private val _mainPlaceList = MutableStateFlow<List<PlaceDetail>>(emptyList())
    val mainPlaceList: StateFlow<List<PlaceDetail>> get() = _mainPlaceList

    private val _allPlaceList = MutableStateFlow<List<LatLng>>(emptyList())
    val allPlaceList: StateFlow<List<LatLng>> get() = _allPlaceList

    @AssistedFactory
    interface TravelJournalInfoFactory {
        fun create(travelJournalInfo: TravelJournalInfo): TravelJournalMapViewModel
    }

    fun initMainPlaceList() {
        viewModelScope.launch {
            val placeList = travelJournalInfo.travelJournalContents.map { it.placeDetail }

            _mainPlaceList.emit(placeList)
        }
    }

    fun initAllPlaceList() {
        viewModelScope.launch {
            val latitudes = emptyList<Double>().toMutableList()
            val longitudes = emptyList<Double>().toMutableList()
            val placeList = emptyList<LatLng>().toMutableList()

            travelJournalInfo.travelJournalContents.forEach {
                latitudes.plus(it.latitude)
                longitudes.plus(it.longitude)
            }

            for (i in 0 until latitudes.size) {
                placeList.add(LatLng(latitudes[i], longitudes[i]))
            }

            _allPlaceList.emit(placeList)
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalInfoFactory,
            travelJournalInfo: TravelJournalInfo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalInfo) as T
            }
        }

        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
    }
}
