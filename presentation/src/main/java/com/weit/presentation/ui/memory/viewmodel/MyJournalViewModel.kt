package com.weit.presentation.ui.memory.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.user.UserProfile
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.user.GetUserUseCase
import com.weit.presentation.ui.util.EventFlow
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyJournalViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getMyTravelJournalListUseCase: GetMyTravelJournalListUseCase
): ViewModel() {
    private val _myNickname = MutableStateFlow("")
    val myNickname: StateFlow<String> get() = _myNickname

    private val _myProfile = MutableStateFlow<UserProfile?>(null)
    val myProfile: StateFlow<UserProfile?> get() = _myProfile

    private val _myJournals = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val myJournals: StateFlow<List<TravelJournalListInfo>> get() = _myJournals

    private val _randomJournal = MutableStateFlow<TravelJournalListInfo?>(null)
    val randomJournal: StateFlow<TravelJournalListInfo?> get() = _randomJournal

    private val _isEmptyMyJournal = MutableStateFlow<Boolean>(true)
    val isEmptyMyJournal: StateFlow<Boolean> get() = _isEmptyMyJournal

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        getMyInfo()
        getMyJournal()
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            val result = getUserUseCase()

            if (result.isSuccess) {
                val user = result.getOrThrow()
                _myNickname.emit(user.nickname)
                _myProfile.emit(user.profile)
            } else {
                Log.d("getMyInfo", "Get My Info fail : ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getMyJournal() {
        viewModelScope.launch {
            val result =  getMyTravelJournalListUseCase(null, null, null)
            // todo 무한 스크롤 적용 필요

            if (result.isSuccess){
                val list = result.getOrThrow()
                if (list.isEmpty()){
                    _isEmptyMyJournal.emit(true)
                } else {
                    _isEmptyMyJournal.emit(false)
                    val random = list.random()
                    _randomJournal.emit(random)
                }
                _myJournals.emit(list)
            } else {
                Log.d("getMyJournal", "Get My Journal fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun onClickRandomJournal() {
        viewModelScope.launch {
            val id = randomJournal.value?.travelJournalId

            if (id != null){
                _event.emit(Event.MoveToRandomJournal(id))
            }
        }
    }

    sealed class Event {
        data class MoveToRandomJournal(
            val randomJournalId: Long
        ) : Event()
    }
}
