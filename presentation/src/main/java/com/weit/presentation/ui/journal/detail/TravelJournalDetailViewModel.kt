package com.weit.presentation.ui.journal.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weit.domain.model.journal.TravelJournalInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class TravelJournalDetailViewModel @AssistedInject constructor(
    @Assisted private val travelJournalInfo: TravelJournalInfo
)
: ViewModel() {
    @AssistedFactory
    interface TravelJournalInfoFactory{
        fun create(travelJournalInfo: TravelJournalInfo): TravelJournalDetailViewModel
    }

    val journalInfo = travelJournalInfo

    private val _travelJournalModelInfo = MutableStateFlow<TravelJournalModelInfo>(
        TravelJournalModelInfo(TravelJournalModel.Basic, journalInfo)
    )
    val travelJournalModelInfo : StateFlow<TravelJournalModelInfo> get() = _travelJournalModelInfo

    private val _journalModel = MutableStateFlow<TravelJournalModel>(TravelJournalModel.Basic)
    val journalModel : StateFlow<TravelJournalModel> get() = _journalModel


    enum class TravelJournalModel{
        Basic,
        Album
    }

    data class TravelJournalModelInfo (
        private val model : TravelJournalModel,
        private val info : TravelJournalInfo
    )

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalInfoFactory,
            travelJournalInfo: TravelJournalInfo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalInfo) as T
            }
        }
    }
}
