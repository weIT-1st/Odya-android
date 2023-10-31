package com.weit.presentation.ui.map.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.place.GetSearchPlaceUseCase
import com.weit.domain.usecase.placesearchhistory.GetPlaceSearchHistoryUseCase
import com.weit.domain.usecase.placesearchhistory.GetRecentPlaceSearchUseCase
import com.weit.domain.usecase.placesearchhistory.SetRecentPlaceSearchUseCase
import com.weit.presentation.model.HotPlaceRank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSearchTopSheetViewModel @Inject constructor(
    private val getSearchPlaceUseCase: GetSearchPlaceUseCase,
    private val getRecentPlaceSearchUseCase: GetRecentPlaceSearchUseCase,
    private val setRecentPlaceSearchUseCase: SetRecentPlaceSearchUseCase,
    private val getPlaceSearchHistoryUseCase: GetPlaceSearchHistoryUseCase
): ViewModel() {

    val searchTerm = MutableStateFlow("")

    private val _searchPlaceList = MutableStateFlow<List<PlacePrediction>>(emptyList())
    val searchPlaceList: StateFlow<List<PlacePrediction>> get() = _searchPlaceList

    private val _recentSearchList = MutableStateFlow<List<String>>(emptyList())
    val recentSearchList: StateFlow<List<String>> get() = _recentSearchList

    private val _odyaHotPlaceRank = MutableStateFlow<List<HotPlaceRank>>(emptyList())
    val odyaHotPlaceRank : StateFlow<List<HotPlaceRank>> get() = _odyaHotPlaceRank

    init {
        viewModelScope.launch {
            val list : MutableList<HotPlaceRank> = mutableListOf()
            (topRankMinCount..topRankMaxCount).forEach { list.add(HotPlaceRank(it, "")) }
            _odyaHotPlaceRank.emit(list)
        }
    }

    fun searchPlace(searchTerm: String) {
        viewModelScope.launch {
            val result = getSearchPlaceUseCase(searchTerm)
            _searchPlaceList.emit(result)
        }
    }

    fun getRecentPlaceSearch() {
        viewModelScope.launch {
            val result = getRecentPlaceSearchUseCase()
            if (result.isSuccess){
                val list = result.getOrThrow()?: emptyList()
                _recentSearchList.emit(list)
            } else {
                Log.d("searchRecent", "fail : ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun plusRecentPlaceSearch(searchTerm: String){
        viewModelScope.launch {
            val list = recentSearchList.value.toMutableList()
            list.add(searchTerm)
            _recentSearchList.emit(list)
            setRecentPlaceSearchUseCase(list)
        }
    }

    fun deleteAllRecentPlaceSearch(){
        viewModelScope.launch{
            deleteRecentPlace(emptyList())
        }
    }

    fun deleteSomethingRecentPlaceSearch(searchedPlace: String){
        viewModelScope.launch{
            val set = recentSearchList.value
            val newSet = set.filterNot { it == searchedPlace }.toList()
            deleteRecentPlace(newSet)
        }
    }

    private suspend fun deleteRecentPlace(set: List<String>){
        val result = setRecentPlaceSearchUseCase(set)

        if (result.isSuccess){
            _recentSearchList.emit(set)
        } else {
            Log.d("Delete Recent Place Search", "failed")
        }
    }

    fun getOdyaHotPlaceRank(){
        viewModelScope.launch{
            val result = getPlaceSearchHistoryUseCase()

            if (result.isSuccess){
                val list = result.getOrThrow().toList()
                val hotplaceRankList = odyaHotPlaceRank.value.toMutableList()
                list.mapIndexed { index, place ->
                    if (index < topRankMaxCount){
                        hotplaceRankList[index] = HotPlaceRank(index + 1, place)
                    }
                }
                _odyaHotPlaceRank.emit(hotplaceRankList)
            } else {
                Log.d("hot odya", "fail")
            }
        }
    }

    companion object{
        private const val topRankMinCount = 1
        private const val topRankMaxCount = 10
    }

}
