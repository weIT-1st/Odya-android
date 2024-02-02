package com.weit.presentation.ui.journal.travel_journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.usecase.journal.DeleteTravelJournalUseCase
import com.weit.domain.usecase.journal.GetTravelJournalUseCase
import com.weit.domain.usecase.user.GetUserIdUseCase
import com.weit.presentation.model.journal.TravelJournalUpdateDTO
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min

class TravelJournalViewModel @AssistedInject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getTravelJournalUseCase: GetTravelJournalUseCase,
    private val deleteTravelJournalUseCase: DeleteTravelJournalUseCase,
    @Assisted private val travelJournalId: Long=0
) : ViewModel() {
    @AssistedFactory
    interface TravelJournalIdFactory {
        fun create(travelJournalId: Long): TravelJournalViewModel
    }

    private val _journalInfo = MutableStateFlow<TravelJournalInfo?>(null)
    val journalInfo: StateFlow<TravelJournalInfo?> get() = _journalInfo

    private val _travelJournalDetailToolBarInfo = MutableStateFlow(
        TravelJournalDetailToolBarInfo(
            true, null, DEFAULT_JOURNAL_ID)
    )
    val travelJournalDetailToolBarInfo: StateFlow<TravelJournalDetailToolBarInfo> get() = _travelJournalDetailToolBarInfo

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    private var journalId: Long = 0

    fun initialize(savedJournalId : Long?) {
        journalId = if(savedJournalId == null){
            travelJournalId
        }else{
            savedJournalId.toLong()
        }
        initJournalInfo()
    }

    private fun initJournalInfo() {
        viewModelScope.launch {
            val result = getTravelJournalUseCase(journalId)

            if (result.isSuccess) {
                val info = result.getOrThrow()
                _journalInfo.emit(info)

                if (info.writer.userId == getUserIdUseCase()) {
                    _travelJournalDetailToolBarInfo.emit(
                        TravelJournalDetailToolBarInfo(
                            true,
                            "",
                            info.travelJournalId
                        )
                    )
                } else {
                    _travelJournalDetailToolBarInfo.emit(
                        TravelJournalDetailToolBarInfo(
                            false,
                            info.writer.nickname,
                            info.travelJournalId
                        )
                    )
                }
            } else {
                //todo 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun handleFriendsCount(): List<TravelJournalCompanionsInfo>{
        val info = journalInfo.value ?: return emptyList()

        val friendCount = info.travelJournalCompanions.size
        return if (friendCount < MAX_ABLE_SHOW_FRIENDS_NUM) {
            info.travelJournalCompanions
        } else {
            info.travelJournalCompanions.slice(0 until min(MAX_ABLE_SHOW_FRIENDS_NUM, friendCount))
        }
    }

    fun deleteTravelJournal() {
        viewModelScope.launch {
            Log.d("jomi", "click delete")
            val result = deleteTravelJournalUseCase(journalId)

            if (result.isSuccess) {
                _event.emit(Event.DeleteTravelJournalSuccess)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun popUpJournalMenu() {
        viewModelScope.launch {
            _event.emit(Event.PopupTravelJournalMenu)
        }
    }

    fun popUpJournalFriends() {
        viewModelScope.launch {
            _event.emit(Event.PopupTravelJournalFriends)
        }
    }

    fun moveToJournalUpdate() {
        viewModelScope.launch {
            val info = journalInfo.value

            val updateDTO = TravelJournalUpdateDTO(
                title = info?.travelJournalTitle,
                travelStartDate = LocalDate.parse(info?.travelStartDate, DateTimeFormatter.ISO_DATE),
                travelEndDate = LocalDate.parse(info?.travelEndDate, DateTimeFormatter.ISO_DATE),
                visibility = info?.visibility
            )
            _event.emit(Event.MoveToJournalUpdate(updateDTO))
        }
    }

    data class TravelJournalDetailToolBarInfo(
        val isMyTravelJournal: Boolean,
        val title: String?,
        val travelJournalId: Long,
    )

    sealed class Event {
        data class MoveToJournalUpdate(
            val travelJournalUpdateDTO : TravelJournalUpdateDTO
        ) : Event()

        object PopupTravelJournalMenu : Event()
        object PopupTravelJournalFriends : Event()
        object DeleteTravelJournalSuccess : Event()
    }

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalIdFactory,
            travelJournalId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalId) as T
            }
        }

        private const val DEFAULT_JOURNAL_ID = 0L
        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
    }
}
