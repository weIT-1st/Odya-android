package com.weit.presentation.ui.journal.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.bookmark.CreateJournalBookMarkUseCase
import com.weit.domain.usecase.bookmark.DeleteJournalBookMarkUseCase
import com.weit.domain.usecase.bookmark.GetMyJournalBookMarkUseCase
import com.weit.domain.usecase.journal.GetTaggedTravelJournalListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class OtherJournalViewModel @Inject constructor(
    private val getMyJournalBookMarkUseCase: GetMyJournalBookMarkUseCase,
    private val getTaggedTravelJournalListUseCase: GetTaggedTravelJournalListUseCase,
    private val createJournalBookMarkUseCase: CreateJournalBookMarkUseCase,
    private val deleteJournalBookMarkUseCase: DeleteJournalBookMarkUseCase
) : ViewModel() {
    private val _bookMarkTravelJournals = MutableStateFlow<List<JournalBookMarkInfo>>(emptyList())
    val bookMarkTravelJournals: StateFlow<List<JournalBookMarkInfo>> get() = _bookMarkTravelJournals

    private val _taggedTravelJournals = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val taggedTravelJournals: StateFlow<List<TravelJournalListInfo>> get() = _taggedTravelJournals

    private val bookMarkJournals = CopyOnWriteArrayList<JournalBookMarkInfo>()
    private val taggedJournals = CopyOnWriteArrayList<TravelJournalListInfo>()

    private var bookMarkLastId: Long? = null
    private var taggedLastId: Long? = null
    private var bookMarkPageJob: Job = Job().apply {
        complete()
    }
    private var taggedPageJob: Job = Job().apply {
        complete()
    }

    init {
        onNextTaggedJournal()
        onNextBookMarkJournal()
    }

    fun onNextBookMarkJournal() {
        if (bookMarkPageJob.isCompleted.not()) {
            return
        }
        loadNextBookMarkJournals()
    }

    private fun loadNextBookMarkJournals() {
        bookMarkPageJob = viewModelScope.launch {
            val result = getMyJournalBookMarkUseCase(null, lastId = bookMarkLastId, null)

            if (result.isSuccess) {
                val newJournals = result.getOrThrow()

                if (newJournals.isNotEmpty()) {
                    bookMarkLastId = newJournals.last().travelJournalId
                }

                if (newJournals.isEmpty()) {
                    loadNextBookMarkJournals()
                }

                val originalJournals = bookMarkJournals
                bookMarkJournals.clear()
                bookMarkJournals.addAll(originalJournals + newJournals)
                _bookMarkTravelJournals.emit(bookMarkJournals)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun onNextTaggedJournal() {
        if (taggedPageJob.isCompleted.not()) {
            return
        }
        loadNextTaggedJournals()
    }

    private fun loadNextTaggedJournals() {
        bookMarkPageJob = viewModelScope.launch {
            val result = getTaggedTravelJournalListUseCase(null, lastTravelJournal = taggedLastId)

            if (result.isSuccess) {
                val newJournals = result.getOrThrow()

                if (newJournals.isNotEmpty()) {
                    taggedLastId = newJournals.last().travelJournalId
                }

                if (newJournals.isEmpty()) {
                    loadNextTaggedJournals()
                }

                val originalJournals = taggedJournals
                taggedJournals.clear()
                taggedJournals.addAll(originalJournals + taggedJournals)
                _taggedTravelJournals.emit(taggedJournals)
            } else {
                // TODO 에러 처리
                Logger.t("MainTest").i("${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun updateBookmarkTravelJournalBookmarkState(travelJournalId: Long) {
        viewModelScope.launch {
            val journals = bookMarkTravelJournals.value
            val journal = journals.find { it.travelJournalId == travelJournalId }

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

    fun updateTaggedTravelJournalBookmarkState(travelJournalId: Long) {
        viewModelScope.launch {

            val tageedJournals = taggedTravelJournals.value
            val journal = tageedJournals.find { it.travelJournalId == travelJournalId }

            if (journal?.isBookmarked == null) {
                return@launch
            }

            if (journal.isBookmarked) {
                deleteTravelJournalBookmark(tageedJournals, journal)
            } else {
                createTravelJournalBookmark(tageedJournals, journal)
            }
        }
    }

    private fun createTravelJournalBookmark(journals: List<JournalBookMarkInfo>, journal: JournalBookMarkInfo){
        viewModelScope.launch {
            val result = createJournalBookMarkUseCase(journal.travelJournalId)

            if (result.isSuccess) {
                journals[journals.indexOf(journal)].isBookmarked = true
            }
        }
    }

    private fun deleteTravelJournalBookmark(journals: List<JournalBookMarkInfo>, journal: JournalBookMarkInfo) {
        viewModelScope.launch {
            val result = deleteJournalBookMarkUseCase(journal.travelJournalId)

            if (result.isSuccess) {
                journals[journals.indexOf(journal)].isBookmarked = false
            }
        }
    }

    private fun createTravelJournalBookmark(journals: List<TravelJournalListInfo>, journal: TravelJournalListInfo){
        viewModelScope.launch {
            val result = createJournalBookMarkUseCase(journal.travelJournalId)

            if (result.isSuccess) {
                journals[journals.indexOf(journal)].isBookmarked = true
                _taggedTravelJournals.emit(journals)
            }
        }
    }

    private fun deleteTravelJournalBookmark(journals: List<TravelJournalListInfo>, journal: TravelJournalListInfo) {
        viewModelScope.launch {
            val result = deleteJournalBookMarkUseCase(journal.travelJournalId)

            if (result.isSuccess) {
                journals[journals.indexOf(journal)].isBookmarked = false
                _taggedTravelJournals.emit(journals)
            }
        }
    }





    fun deleteTaggedJournal() {
        // todo 태그된 여행일지 삭제
    }
}
