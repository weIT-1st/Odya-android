package com.weit.presentation.ui.memory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.bookmark.GetMyJournalBookMarkUseCase
import com.weit.domain.usecase.journal.GetTaggedTravelJournalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherJournalViewModel @Inject constructor(
    private val getMyJournalBookMarkUseCase: GetMyJournalBookMarkUseCase,
    private val getTaggedTravelJournalListUseCase: GetTaggedTravelJournalListUseCase
): ViewModel() {
    private val _bookMarkTravelJournals = MutableStateFlow<List<JournalBookMarkInfo>>(emptyList())
    val bookMarkTravelJournals: StateFlow<List<JournalBookMarkInfo>> get() = _bookMarkTravelJournals

    private val _taggedTravelJouranls = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val taggedTravelJournals: StateFlow<List<TravelJournalListInfo>> get() = _taggedTravelJouranls

    init {
        getBookMarkJournal()
        getTaggedJournal()
    }

    private fun getBookMarkJournal(){
        viewModelScope.launch {
            val result = getMyJournalBookMarkUseCase(null, null, null)

            if (result.isSuccess) {
                val list = result.getOrThrow()
                _bookMarkTravelJournals.emit(list)
            } else {
                Log.d("getBookMarkJournal", "Get Bookmark Journal Fail :  ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getTaggedJournal(){
        viewModelScope.launch {
            val result = getTaggedTravelJournalListUseCase(null, null)

            if (result.isSuccess){
                val list = result.getOrThrow()
                _taggedTravelJouranls.emit(list)
            } else {
                Log.d("getTaggedJournal", "Get Tagged Journal Fail : ${result.exceptionOrNull()}")
            }
        }
    }
}
