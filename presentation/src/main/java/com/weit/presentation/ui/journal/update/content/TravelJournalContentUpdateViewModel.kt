package com.weit.presentation.ui.journal.update.content

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.journal.TravelJournalContentUpdateInfo
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.journal.UpdateTravelJournalContentUseCase
import com.weit.presentation.model.journal.TravelJournalContentUpdateDTO
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.model.post.travellog.DailyTravelLog
import com.weit.presentation.ui.post.travellog.PostTravelLogViewModel
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import com.weit.presentation.ui.util.toMillis
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TravelJournalContentUpdateViewModel @AssistedInject constructor(
    @Assisted val travelJournalContentUpdateDTO: TravelJournalContentUpdateDTO,
    private val updateTravelJournalContentUseCase: UpdateTravelJournalContentUseCase
): ViewModel() {
    @AssistedFactory
    interface TravelJournalContentUpdateFactory{
        fun create(travelJournalContentUpdateDTO: TravelJournalContentUpdateDTO) : TravelJournalContentUpdateViewModel
    }

    val content = MutableStateFlow(travelJournalContentUpdateDTO.content)

    private val _date = MutableStateFlow(travelJournalContentUpdateDTO.travelJournalContentDate)
    val date : StateFlow<LocalDate> get() = _date

    private val _placeName = MutableStateFlow(travelJournalContentUpdateDTO.placeName)
    val placeName : StateFlow<String?> get() = _placeName
    private val _placeId = MutableStateFlow(travelJournalContentUpdateDTO.placeID)
    val placeId : StateFlow<String?> get() = _placeId

    private val _images = MutableStateFlow(travelJournalContentUpdateDTO.updateContentImages)
    val images : StateFlow<List<String>> get() = _images

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onPickDailyDate() {
        viewModelScope.launch {
            val date = travelJournalContentUpdateDTO.travelJournalContentDate
            val minDateMillis = travelJournalContentUpdateDTO.travelJournalStart.toMillis()
            val maxDateMillis = travelJournalContentUpdateDTO.travelJournalEnd.toMillis()
            _event.emit(Event.ShowDailyDatePicker(date, minDateMillis, maxDateMillis ))
        }
    }

    fun onDailyDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        viewModelScope.launch {
            val selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            _date.emit(selectedDate)
        }
    }

    fun onDatePickerDismissed() {
        viewModelScope.launch {
            _event.emit(Event.ClearDatePickerDialog)
        }
    }

    fun onSelectPictures(pickImageUseCase: PickImageUseCase) {
        viewModelScope.launch {
            val selectedPicture = pickImageUseCase()
            val currentImage = images.value
            _images.emit(currentImage + selectedPicture)
        }
    }

    fun deletePicture(newImages : List<String>){
        viewModelScope.launch {
            _images.emit(newImages)
        }
    }
    fun updateTravelJournalContent() {
        viewModelScope.launch{
            Log.d("jomi", "click update")
            val newDate = date.value
            val newContent = content.value
            val newPlaceId = placeId.value
            val newImages = images.value

            if (newContent.isNullOrBlank()) {
                _event.emit(Event.NoContentData)
                return@launch
            }

            if (newImages.isEmpty()) {
                _event.emit(Event.NoImagesData)
                return@launch
            }

            val result = updateTravelJournalContentUseCase(
                travelJournalContentUpdateDTO.travelJournalId,
                travelJournalContentUpdateDTO.travelJournalContentId,
                TravelJournalContentUpdateInfo(
                    newContent,
                    newPlaceId,
                    emptyList(), // latitude
                    emptyList(), // longitude
                    newDate,
                    emptyList(), // image name
                    emptyList() // image id
                ),
                newImages
            )
        }
    }

    sealed class Event {
        data class ShowDailyDatePicker(
            val currentDate: LocalDate?,
            val minDateMillis: Long,
            val maxDateMillis: Long,
        ) : Event()

        data class ShowSelectPlace(
            val placePredictionDTO : List<PlacePredictionDTO>
        ): Event()

        object ClearDatePickerDialog : Event()
        object NoContentData : Event()
        object NoImagesData : Event()
    }
    companion object {
        fun provideFactory(
            assistedFactory: TravelJournalContentUpdateFactory,
            travelJournalContentUpdateDTO: TravelJournalContentUpdateDTO
        ) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(travelJournalContentUpdateDTO) as T
            }
        }
    }
}
