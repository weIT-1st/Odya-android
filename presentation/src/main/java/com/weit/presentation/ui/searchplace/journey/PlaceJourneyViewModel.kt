package com.weit.presentation.ui.searchplace.journey

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.journal.GetFriendTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetRecommendTravelJournalListUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaceJourneyViewModel @AssistedInject constructor(
    private val getMyTravelJournalListUseCase: GetMyTravelJournalListUseCase,
    private val getFriendTravelJournalListUseCase: GetFriendTravelJournalListUseCase,
    private val getRecommendTravelJournalListUseCase: GetRecommendTravelJournalListUseCase,
    @Assisted private val placeId: String
) : ViewModel(){

    private val _myJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val myJournalList: StateFlow<List<TravelJournalListInfo>> get() = _myJournalList

    private val _friendJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val friendJournalList: StateFlow<List<TravelJournalListInfo>> get() = _friendJournalList

    private val _recommendJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val recommendJournalList: StateFlow<List<TravelJournalListInfo>> get() = _recommendJournalList

    @AssistedFactory
    interface PlaceIdFactory{
        fun create(placeId: String): PlaceJourneyViewModel
    }

    init {
        getMyJournalList()
        getFriendJournalList()
        getRecommendJournalList()
    }

    private fun getMyJournalList(){
        viewModelScope.launch {

            // todo 무한 스크롤
            val result = getMyTravelJournalListUseCase(null, null, placeId)
            if (result.isSuccess){
                val list = result.getOrThrow()
                _myJournalList.emit(list)
            } else {
                Log.d("getMyJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getFriendJournalList(){
        viewModelScope.launch{
            // todo 무한 스크롤
            val result = getFriendTravelJournalListUseCase(null, null)
            if (result.isSuccess){
                val list = result.getOrThrow()
                _friendJournalList.emit(list)
            } else {
                Log.d("getFriendJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getRecommendJournalList(){
        viewModelScope.launch {
            // todo 무한 스크롤
            val result = getRecommendTravelJournalListUseCase(null, null)
            if (result.isSuccess){
                val list = result.getOrThrow()
                _recommendJournalList.emit(list)
            } else {
                Log.d("getRecommendJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    companion object{
        fun provideFactory(
           assistedFactory: PlaceIdFactory,
           placeId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeId) as T
            }
        }
    }
}
