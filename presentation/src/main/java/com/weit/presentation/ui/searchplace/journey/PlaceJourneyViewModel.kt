package com.weit.presentation.ui.searchplace.journey

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.journal.GetFriendTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetRecommendTravelJournalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceJourneyViewModel @Inject constructor(
    private val getMyTravelJournalListUseCase: GetMyTravelJournalListUseCase,
    private val getFriendTravelJournalListUseCase: GetFriendTravelJournalListUseCase,
    private val getRecommendTravelJournalListUseCase: GetRecommendTravelJournalListUseCase
) : ViewModel(){
    private val _myJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val myJournalList: StateFlow<List<TravelJournalListInfo>> get() = _myJournalList

    private val _friendJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val friendJournalList: StateFlow<List<TravelJournalListInfo>> get() = _friendJournalList

    private val _recommendJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val recommendJournalList: StateFlow<List<TravelJournalListInfo>> get() = _recommendJournalList

    init {
        getMyJournalList()
        getFriendJournalList()
        getRecommendJournalList()
    }

    private fun getMyJournalList(){
        viewModelScope.launch {
            val result = getMyTravelJournalListUseCase(null, null)
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
            val result = getRecommendTravelJournalListUseCase(null, null)
            if (result.isSuccess){
                val list = result.getOrThrow()
                _recommendJournalList.emit(list)
            } else {
                Log.d("getRecommendJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }
}
