package com.weit.presentation.ui.journal.travel_journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.usecase.journal.GetTravelJournalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelJournalViewModel @Inject constructor(
    private val getTravelJournalUseCase: GetTravelJournalUseCase
) : ViewModel(){

    private val _journalImage = MutableStateFlow<TravelJournalInfo?>(null)
    val journalImage: StateFlow<TravelJournalInfo?> get() = _journalImage

    init {

    }

    private fun getJournal(){
        viewModelScope.launch {
            val result = getTravelJournalUseCase(0)

            if (result.isSuccess){
                val info = result.getOrThrow()
                _journalImage.emit(info)
            } else {
                Log.d("getJournalInfo", "Get Journal Info Faill : ${result.exceptionOrNull()}")
            }
        }
    }
}
