package com.weit.presentation.ui.journal.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.user.User
import com.weit.domain.usecase.bookmark.CreateJournalBookMarkUseCase
import com.weit.domain.usecase.bookmark.DeleteJournalBookMarkUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class MyJournalViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getMyTravelJournalListUseCase: GetMyTravelJournalListUseCase,
    private val createJournalBookMarkUseCase: CreateJournalBookMarkUseCase,
    private val deleteJournalBookMarkUseCase: DeleteJournalBookMarkUseCase
): ViewModel() {

    private val _myProfile = MutableStateFlow<User?>(null)
    val myProfile: StateFlow<User?> get() = _myProfile

    private val _myJournals = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val myJournals: StateFlow<List<TravelJournalListInfo>> get() = _myJournals

    private val _randomJournal = MutableStateFlow<TravelJournalListInfo?>(null)
    val randomJournal: StateFlow<TravelJournalListInfo?> get() = _randomJournal

    private val _isEmptyMyJournal = MutableStateFlow(true)
    val isEmptyMyJournal: StateFlow<Boolean> get() = _isEmptyMyJournal

    private val journals = CopyOnWriteArrayList<TravelJournalListInfo>()
    private var journalLastId: Long? = null
    private var pageJob: Job = Job().apply {
        complete()
    }

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getMyInfo()
        onNextJournal()
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            val result = getUserUseCase()

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _myProfile.emit(user)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun onNextJournal() {
        if (pageJob.isCompleted.not()) {
            return
        }
        loadNextMyJournal()
    }

    private fun loadNextMyJournal() {
        pageJob = viewModelScope.launch {
            val result = getMyTravelJournalListUseCase(
                size = DEFAULT_PAGE,
                lastTravelJournal = journalLastId,
                null
            )

            if (result.isSuccess) {
                val newJournals = result.getOrThrow()

                if (newJournals.isNotEmpty()) {
                    journalLastId = newJournals.last().travelJournalId
                }

                journals.addAll(newJournals)

                _myJournals.emit(journals)
                _randomJournal.emit(journals.randomOrNull())
                _isEmptyMyJournal.emit(journals.isEmpty())

            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }



    fun updateTravelJournalBookmarkState(travelJournalId: Long) {
        viewModelScope.launch{
            val journals = myJournals.value
            val journal = myJournals.value.find { it.travelJournalId == travelJournalId }

            if (journal?.isBookmarked == null) {
                return@launch
            }

            if (journal.isBookmarked) {
                deleteTravelJournalBookmark(journals, journal)
            } else {
                createTravelJournalBookmark(journals, journal)
            }
        }
    }

    private fun createTravelJournalBookmark(journals: List<TravelJournalListInfo>, journal: TravelJournalListInfo){
        viewModelScope.launch {
            val result = createJournalBookMarkUseCase(journal.travelJournalId)

            if (result.isSuccess) {
                journals[journals.indexOf(journal)].isBookmarked = true
                _myJournals.emit(journals)
            }
        }
    }

    private fun deleteTravelJournalBookmark(journals: List<TravelJournalListInfo>, journal: TravelJournalListInfo) {
        viewModelScope.launch {
            val result = deleteJournalBookMarkUseCase(journal.travelJournalId)

            if (result.isSuccess) {
                journals[journals.indexOf(journal)].isBookmarked = false
                _myJournals.emit(journals)
            }
        }
    }

    fun moveToRandomJournal() {
        viewModelScope.launch {
            val id = randomJournal.value?.travelJournalId

            if (id != null){
                _event.emit(Event.MoveToRandomJournal(id))
            }
        }
    }

    fun moveToJournal(id: Long){
        viewModelScope.launch {
            _event.emit(Event.MoveToJournal(id))
        }
    }

    sealed class Event {
        data class MoveToRandomJournal(
            val randomJournalId: Long
        ) : Event()

        data class MoveToJournal(
            val travelJournalId: Long
        ) : Event()
    }

    companion object {
        const val DEFAULT_PAGE = 10
    }
}
