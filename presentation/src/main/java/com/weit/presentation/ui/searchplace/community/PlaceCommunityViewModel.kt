package com.weit.presentation.ui.searchplace.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.community.CommunityMainContent
import com.weit.domain.model.community.CommunityMyActivityContent
import com.weit.domain.model.community.CommunityRequestInfo
import com.weit.domain.usecase.community.GetCommunitiesUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaceCommunityViewModel @AssistedInject constructor(
    private val getCommunitiesUseCase: GetCommunitiesUseCase,
    @Assisted private val placeId: String
) : ViewModel() {

    private val _postImages = MutableStateFlow<List<CommunityMainContent>>(emptyList())
    val postImages: StateFlow<List<CommunityMainContent>> get() = _postImages

    private var getJob: Job = Job().apply {
        complete()
    }

    private var communityLastId : Long? = null

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface PlaceIdFactory{
        fun create(placeId: String): PlaceCommunityViewModel
    }

    init {
        onNextImages()
    }

    fun onNextImages(){
        if (getJob.isCompleted.not()){
            return
        }
        loadNextCommunityMyActivity()
    }

    private fun loadNextCommunityMyActivity() {
        getJob = viewModelScope.launch {
            val result = getCommunitiesUseCase(
                CommunityRequestInfo(DEFAULT_PAGE_SIZE, communityLastId, null, placeId)
            )

            if (result.isSuccess) {
                val newImages = result.getOrThrow()
                communityLastId = newImages.last().communityId
                if (newImages.isEmpty()){
                    onNextImages()
                }
                _postImages.emit(postImages.value + newImages)
            } else {
                Log.d("test", "getCommunityFail : ${result.exceptionOrNull()}")
            }
        }
    }

    sealed class Event {

    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 18

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
