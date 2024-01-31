package com.weit.presentation.ui.main.journal

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.journal.GetFriendTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetMyTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetRecommendTravelJournalListUseCase
import com.weit.domain.usecase.journal.GetTravelJournalUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaceJournalViewModel @AssistedInject constructor(
    private val getMyTravelJournalListUseCase: GetMyTravelJournalListUseCase,
    private val getFriendTravelJournalListUseCase: GetFriendTravelJournalListUseCase,
    private val getRecommendTravelJournalListUseCase: GetRecommendTravelJournalListUseCase,
    @Assisted private val placeId: String
) : ViewModel(){

    private val _myRandomJournal = MutableStateFlow<TravelJournalListInfo?>(null)
    val myRandomJournal : StateFlow<TravelJournalListInfo?> get() = _myRandomJournal

    val myJournalContent = MutableStateFlow("")

    private val _friendJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val friendJournalList: StateFlow<List<TravelJournalListInfo>> get() = _friendJournalList

    private val _recommendJournalList = MutableStateFlow<List<TravelJournalListInfo>>(emptyList())
    val recommendJournalList: StateFlow<List<TravelJournalListInfo>> get() = _recommendJournalList

    private var friendLastID : Long? = null
    private var recommendLastID : Long? = null

    private var friendJob: Job = Job().apply { complete() }
    private var recommendJob: Job = Job().apply { complete() }
    @AssistedFactory
    interface PlaceIdFactory{
        fun create(placeId: String): PlaceJournalViewModel
    }

    init {
        getMyJournalList()
        onNextFriendJournal()
        onNextRecommendJournal()
    }

    fun onNextFriendJournal() {
        if (friendJob.isCompleted.not()) {
            return
        }
        loadNextFriendJournal()
    }

    private fun loadNextFriendJournal() {
        friendJob = viewModelScope.launch {
            val result = getFriendTravelJournalListUseCase(DEFAULT_PAGE_SIZE, friendLastID, placeId)

            if (result.isSuccess) {
                val newFriendJournals = result.getOrThrow()
                newFriendJournals.lastOrNull()?.let {
                    friendLastID = it.travelJournalId
                    _friendJournalList.emit(friendJournalList.value + newFriendJournals)
                }
            } else {
                Log.d("getFriendJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun onNextRecommendJournal() {
        if (recommendJob.isCompleted.not()) {
            return
        }
        loadNextRecommendJournal()
    }

    private fun loadNextRecommendJournal() {
        recommendJob = viewModelScope.launch {
            val result = getRecommendTravelJournalListUseCase(DEFAULT_PAGE_SIZE, recommendLastID, placeId)

            if (result.isSuccess) {
                val newRecommendJournals = result.getOrThrow()
                newRecommendJournals.lastOrNull()?.let {
                    recommendLastID = it.travelJournalId
                    _recommendJournalList.emit(recommendJournalList.value + newRecommendJournals)
                }
            } else {
                Log.d("getRecommendJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getMyJournalList(){
        viewModelScope.launch {
            val result = getMyTravelJournalListUseCase(null, null, placeId)
            if (result.isSuccess){
                val random = result.getOrThrow().randomOrNull()
                _myRandomJournal.emit(random)

                setMyJournalContent()
            } else {
                Log.d("getMyJournalList", "fail : ${result.exceptionOrNull()}")
            }
        }
    }

    private fun setMyJournalContent() {
        viewModelScope.launch {
            val journal = myRandomJournal.value
            journal?.content?.let {
                myJournalContent.emit(it)
            }
        }
    }

    companion object{
        const val DEFAULT_PAGE_SIZE = 10
        fun provideFactory(
           assistedFactory: PlaceIdFactory,
           placeId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeId) as T
            }
        }
    }
}
