package com.weit.presentation.ui.journal.album

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.usecase.journal.DeleteTravelJournalContentUseCase
import com.weit.presentation.model.journal.TravelJournalContentUpdateDTO
import com.weit.presentation.model.journal.TravelJournalDetailInfo
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

class TravelJournalAlbumViewModel @AssistedInject constructor(
    @Assisted private val travelJournalInfo: TravelJournalInfo,
    private val deleteTravelJournalContentUseCase: DeleteTravelJournalContentUseCase,
): ViewModel() {
    @AssistedFactory
    interface TravelJournalInfoFactory{
        fun create(travelJournalInfo: TravelJournalInfo): TravelJournalAlbumViewModel
    }

    private val _journalContents = MutableStateFlow<List<TravelJournalDetailInfo>>(emptyList())
    val journalContents: StateFlow<List<TravelJournalDetailInfo>> get() = _journalContents

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        initAlbumJournalInfo()
    }

    private fun initAlbumJournalInfo() {
        viewModelScope.launch {
            val albumJournalInfo = travelJournalInfo.travelJournalContents.map {
                TravelJournalDetailInfo(
                    it.travelJournalContentId,
                    it.travelDate,
                    it.content,
                    it.placeDetail.name,
                    it.placeDetail.address?.split(" ")?.slice(1..2)?.joinToString(" "),
                    it.travelJournalContentImages
                )
            }
            _journalContents.emit(albumJournalInfo)
        }
    }

    fun deleteContent(contentId: Long) {
        viewModelScope.launch {
            val result = deleteTravelJournalContentUseCase(travelJournalInfo.travelJournalId, contentId)

            if (result.isSuccess) {
                _event.emit(Event.DeleteTravelJournalContent)
            } else {
                Log.d("delete journal content", "delete content error : ${result.exceptionOrNull()}")
            }
        }
    }

    fun updateTravelJournalContent(contentId: Long) {
        viewModelScope.launch {
            val info = travelJournalInfo.travelJournalContents.find { it.travelJournalContentId == contentId }
            info?.let {
                val travelJournalUpdateDTO = TravelJournalContentUpdateDTO(
                    travelJournalInfo.travelJournalId,
                    info.travelJournalContentId,
                    LocalDate.parse(info.travelDate, DateTimeFormatter.ISO_DATE),
                    LocalDate.parse(travelJournalInfo.travelStartDate, DateTimeFormatter.ISO_DATE),
                    LocalDate.parse(travelJournalInfo.travelEndDate, DateTimeFormatter.ISO_DATE),
                    info.content,
                    info.placeDetail.name,
                    info.placeDetail.placeId,
                    info.latitude,
                    info.longitude,
                    info.travelJournalContentImages.map { it.contentImageUrl },
                    info.travelJournalContentImages.map { it.travelJournalContentImageId }
                )
                _event.emit(Event.MoveToUpdate(travelJournalUpdateDTO))
            }
        }
    }

    sealed class Event{
        data class MoveToUpdate(
            val travelJournalContentUpdateDTO: TravelJournalContentUpdateDTO
        ): Event()
        object DeleteTravelJournalContent: Event()
    }

    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalInfoFactory,
            travelJournalInfo: TravelJournalInfo
        ) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalInfo) as T
            }
        }
    }
}
