package com.weit.presentation.ui.map.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.place.PlacePrediction
import com.weit.domain.usecase.place.GetSearchPlaceUseCase
import com.weit.domain.usecase.placesearchhistory.DeleteAllRecentPlaceSearchUseCase
import com.weit.domain.usecase.placesearchhistory.GetPlaceSearchHistoryUseCase
import com.weit.domain.usecase.placesearchhistory.GetRecentPlaceSearchUseCase
import com.weit.domain.usecase.placesearchhistory.RegisterPlaceSearchHistoryUseCase
import com.weit.domain.usecase.placesearchhistory.SetRecentPlaceSearchUseCase
import com.weit.presentation.model.HotPlaceRank
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class MainSearchTopSheetViewModel @Inject constructor(
    private val getSearchPlaceUseCase: GetSearchPlaceUseCase,
    private val getRecentPlaceSearchUseCase: GetRecentPlaceSearchUseCase,
    private val setRecentPlaceSearchUseCase: SetRecentPlaceSearchUseCase,
    private val getPlaceSearchHistoryUseCase: GetPlaceSearchHistoryUseCase,
    private val registerPlaceSearchHistoryUseCase: RegisterPlaceSearchHistoryUseCase,
    private val deleteAllRecentPlaceSearchUseCase: DeleteAllRecentPlaceSearchUseCase
) : ViewModel() {

    val searchTerm = MutableStateFlow(BLANK_SEARCH_TERM)

    private val _searchPlaceList = MutableStateFlow<List<PlacePrediction>>(emptyList())
    val searchPlaceList: StateFlow<List<PlacePrediction>> get() = _searchPlaceList

    private val _recentSearchWords = MutableStateFlow<List<String>>(emptyList())
    val recentSearchWords: StateFlow<List<String>> get() = _recentSearchWords

    private val _odyaHotPlaceRank = MutableStateFlow<List<HotPlaceRank>>(emptyList())
    val odyaHotPlaceRank: StateFlow<List<HotPlaceRank>> get() = _odyaHotPlaceRank

    private val _searchFocus = MutableStateFlow<Boolean>(false)
    val searchFocus: StateFlow<Boolean> get() = _searchFocus

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        viewModelScope.launch {
            val list: MutableList<HotPlaceRank> = mutableListOf()
            (TOP_RANK_MIN_COUNT..TOP_RANK_MAX_COUNT).forEach { list.add(HotPlaceRank(it, BLANK_SEARCH_TERM)) }
            _odyaHotPlaceRank.emit(list)
        }

        getRecentPlaceSearchWord()
        getOdyaHotPlaceRank()
    }

    fun changeMainSearchFocus(focus: Boolean) {
        viewModelScope.launch {
            _searchFocus.emit(focus)
        }
    }

    fun searchPlace(placeId: String) {
        viewModelScope.launch {
            val result = getSearchPlaceUseCase(placeId)
            _searchPlaceList.emit(result)
        }
    }

    private fun getRecentPlaceSearchWord() {
        viewModelScope.launch {
            val result = getRecentPlaceSearchUseCase()
            if (result.isSuccess) {
                val list = result.getOrThrow()
                _recentSearchWords.emit(list)
            } else {
                Log.d("searchRecent", "fail : ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun onSearchNewWord(searchTerm: String) {
        registerPlaceSearchWord(searchTerm)
        plusRecentPlaceSearchWord(searchTerm)
    }

    private fun registerPlaceSearchWord(searchTerm: String) {
        viewModelScope.launch {
            val result = registerPlaceSearchHistoryUseCase(searchTerm)
            if (result.isSuccess) {
                Log.d("Register Place Search", "Register new term success")
            } else {
                Log.d("Register Place Search", "Register new term failed")
            }
        }
    }

    private fun plusRecentPlaceSearchWord(searchTerm: String) {
        viewModelScope.launch {
            val list = recentSearchWords.value

            val newList = if (list.contains(searchTerm)) {
                listOf(searchTerm).plus(list.filter { it != searchTerm })
            } else {
                listOf(searchTerm).plus(list)
            }

            val result = setRecentPlaceSearchUseCase(newList)
            if (result.isSuccess) {
                _recentSearchWords.emit(newList)
                _event.emit(Event.SuccessPlusRecentSearch)
            } else {
                Log.d("Add Recent Place Search Word", "failed")
            }
        }
    }

    fun deleteAllRecentPlaceSearchWord() {
        viewModelScope.launch {
            val result = deleteAllRecentPlaceSearchUseCase()

            if (result.isSuccess) {
                _recentSearchWords.emit(emptyList())
            } else {
                Log.d("Delete Recent Place Search Word", "failed")
            }
        }
    }

    fun deleteRecentPlaceSearch(searchedPlace: String) {
        viewModelScope.launch {
            val list = recentSearchWords.value
            val newList = list.filterNot { it == searchedPlace }

            val result = setRecentPlaceSearchUseCase(newList)

            if (result.isSuccess) {
                _recentSearchWords.emit(newList)
            } else {
                Log.d("Delete Recent Place Search", "failed")
            }
        }
    }

    fun onTouchRecentWord(recentWord: String) {
        viewModelScope.launch {
            searchTerm.emit(recentWord)
            _searchFocus.emit(true)
        }
    }

    fun setBTNPleaseSearchCancelOnClickListener() {
        viewModelScope.launch {
            val hasFocus = searchFocus.value
            if (hasFocus) {
                searchTerm.emit("")
                _event.emit(Event.ClickSearchCancelHasFocus)
            } else {
                _event.emit(Event.ClickSearchCancelHasNotFocus)
            }
        }
    }

    fun getOdyaHotPlaceRank() {
        viewModelScope.launch {
            val result = getPlaceSearchHistoryUseCase()

            if (result.isSuccess) {
                val list = result.getOrThrow()
                val hotplaceRankList = odyaHotPlaceRank.value.toMutableList()

                list.forEachIndexed { index, place ->
                    if (index < hotplaceRankList.size) {
                        hotplaceRankList[index] = HotPlaceRank(index + 1, place)
                    }
                }

                _odyaHotPlaceRank.emit(hotplaceRankList)
            } else {
                Log.d("hot odya", "fail")
            }
        }
    }

//    fun onDestroyView() {
//        viewModelScope.launch {
//            changeMainSearchFocus(false)
//            searchTerm.emit(BLANK_SEARCH_TERM)
//        }
//    }

    sealed class Event {
        object ClickSearchCancelHasFocus : Event()
        object ClickSearchCancelHasNotFocus : Event()
        object SuccessPlusRecentSearch : Event()
    }

    companion object {
        private const val TOP_RANK_MIN_COUNT = 1
        private const val TOP_RANK_MAX_COUNT = 10
        private const val BLANK_SEARCH_TERM = ""
    }
}
