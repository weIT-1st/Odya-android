package com.weit.presentation.ui.journal.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.model.journal.TravelJournalDetailInfo
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

    private val _journalContents = MutableStateFlow<List<TravelJournalDetailInfo>>(emptyList())
    val journalContents: StateFlow<List<TravelJournalDetailInfo>> get() = _journalContents

    init {
        initContentsInfo()
    }

    private fun initContentsInfo() {
        viewModelScope.launch {
            val basicJournalInfo = travelJournalInfo.travelJournalContents.map {
                TravelJournalDetailInfo(
                    it.travelJournalContentId,
                    it.travelDate,
                    it.content,
                    it.placeDetail.name,
                    it.placeDetail.address?.split(" ")?.slice(1..2)?.joinToString(" "),
                    it.travelJournalContentImages
                )
            }
            _journalContents.emit(basicJournalInfo)
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
