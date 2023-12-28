package com.weit.presentation.ui.journal.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalContentsInfo
import com.weit.domain.model.journal.TravelJournalInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TravelJournalBasicViewModel @AssistedInject constructor(
    @Assisted private val travelJournalInfo: TravelJournalInfo
): ViewModel() {

    @AssistedFactory
    interface TravelJournalInfoFactory{
        fun crate(travelJournalInfo: TravelJournalInfo): TravelJournalBasicViewModel
    }

    private val _journalInfo = MutableStateFlow<TravelJournalInfo>(travelJournalInfo)
    val journalInfo: StateFlow<TravelJournalInfo> get() = _journalInfo

    private val _journalContents = MutableStateFlow<List<TravelJournalContentsInfo>>(emptyList())
    val journalContents: StateFlow<List<TravelJournalContentsInfo>> get() = _journalContents

    init {
        getContentsInfo()
    }

    private fun getContentsInfo() {
        viewModelScope.launch {
            val journal = journalInfo.value
            _journalContents.emit(journal.travelJournalContents)
        }
    }
    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalInfoFactory,
            travelJournalInfo: TravelJournalInfo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.crate(travelJournalInfo) as T
            }
        }
    }
}
