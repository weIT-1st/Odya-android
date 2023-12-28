package com.weit.presentation.ui.journal.travel_journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.usecase.journal.GetTravelJournalUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TravelJournalViewModel @AssistedInject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getTravelJournalUseCase: GetTravelJournalUseCase,
    @Assisted private val travelJournalId: Long
) : ViewModel(){
    @AssistedFactory
    interface TravelJournalIdFactory{
        fun create(travelJournalId: Long): TravelJournalViewModel
    }

    private val _journalInfo = MutableStateFlow<TravelJournalInfo?>(null)
    val journalInfo: StateFlow<TravelJournalInfo?> get() = _journalInfo

    private val _isMyJournal = MutableStateFlow(false)
    val isMyJournal: StateFlow<Boolean> get() = _isMyJournal

    init {
        getJournalInfo()
    }

    private fun getJournalInfo(){
        viewModelScope.launch {
            val result = getTravelJournalUseCase(travelJournalId)

            if (result.isSuccess){
                val info = result.getOrThrow()
                _journalInfo.emit(info)

                if (info.writer.userId == getUserIdUseCase()) {
                    _isMyJournal.emit(true)
                } else {
                    _isMyJournal.emit(false)
                }
            } else {
                Log.d("getJournalInfo", "Get Journal Info Fail : ${result.exceptionOrNull()}")
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalIdFactory,
            travelJournalId : Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalId) as T
            }
        }
    }
}
