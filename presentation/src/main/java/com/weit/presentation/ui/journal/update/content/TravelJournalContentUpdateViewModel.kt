package com.weit.presentation.ui.journal.update.content

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.weit.domain.model.journal.TravelJournalContentUpdateInfo
import com.weit.domain.usecase.coordinate.GetStoredCoordinatesUseCase
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.domain.usecase.journal.UpdateTravelJournalContentUseCase
import com.weit.presentation.model.journal.TravelJournalContentUpdateDTO
import com.weit.presentation.model.post.place.PlacePredictionDTO
import com.weit.presentation.model.post.place.SelectPlaceDTO
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
    private val updateTravelJournalContentUseCase: UpdateTravelJournalContentUseCase,
    private val getStoredCoordinatesUseCase: GetStoredCoordinatesUseCase
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

    private val _deleteImages = MutableStateFlow<List<Long>>(emptyList())
    val deleteImages : StateFlow<List<Long>> get() = _deleteImages


    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun onPickDailyDate() {
        viewModelScope.launch {
            val date = travelJournalContentUpdateDTO.travelJournalContentDate
            val minDateMillis = travelJournalContentUpdateDTO.travelJournalStart.toMillis()
            val maxDateMillis = travelJournalContentUpdateDTO.travelJournalEnd.toMillis()
            Log.d("jomi", "$minDateMillis / $maxDateMillis")
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

    fun deletePicture(deleteImage: String, newImages : List<String>){
        viewModelScope.launch {
            _images.emit(newImages)
            val deleteImageIndex = travelJournalContentUpdateDTO.updateContentImages.indexOf(deleteImage)

            val currentDeleteImages = deleteImages.value
            val newDeleteImages = currentDeleteImages.toMutableList().plus(travelJournalContentUpdateDTO.updateContentImageIds[deleteImageIndex])
            _deleteImages.emit(newDeleteImages)
        }
    }

    private suspend fun getStoredCoordinates(date: LocalDate?): List<LatLng> {
        if (date == null) {
            return emptyList()
        }

        val start: Long = date.toMillis()
        val end = date.plusDays(1).toMillis()

        return getStoredCoordinatesUseCase(start, end).map { LatLng(it.lat.toDouble(), it.lng.toDouble()) }
    }

    fun updateTravelJournalContent() {
        viewModelScope.launch{
            val newDate = date.value
            val newContent = content.value
            val newPlaceId = placeId.value
            val allImage = images.value
            val newImages = allImage.filterNot { travelJournalContentUpdateDTO.updateContentImages.contains(it) }
            val newDeleteImages = deleteImages.value
            val latLngs = getStoredCoordinates(newDate)

            if (newContent.isNullOrBlank()) {
                _event.emit(Event.NoContentData)
                return@launch
            }

            if (allImage.isEmpty()) {
                _event.emit(Event.NoImagesData)
                return@launch
            }

            val result = updateTravelJournalContentUseCase(
                travelJournalContentUpdateDTO.travelJournalId,
                travelJournalContentUpdateDTO.travelJournalContentId,
                TravelJournalContentUpdateInfo(
                    newContent,
                    newPlaceId,
                    latLngs.map { it.latitude },
                    latLngs.map { it.longitude },
                    newDate.toString(),
                    newImages.map {
                        it.split("/").last() + IMAGE_EXTENSION_WEBP
                    },
                    newDeleteImages
                ),
                newImages
            )

            if (result.isSuccess) {
                _event.emit(Event.SuccessUpdate(travelJournalContentUpdateDTO.travelJournalId))
            } else {
                //todo 에러처리
                Log.d("update travel",
                    "update Travel Journal Fail : ${result.exceptionOrNull()}")
            }
        }
    }

    fun showSelectedPlace() {
        viewModelScope.launch {
            val dto = TravelJournalContentUpdateDTO(
                travelJournalContentUpdateDTO.travelJournalId,
                travelJournalContentUpdateDTO.travelJournalContentId,
                travelJournalContentUpdateDTO.travelJournalStart,
                travelJournalContentUpdateDTO.travelJournalEnd,
                date.value,
                content.value,
                placeName.value,
                placeId.value,
                travelJournalContentUpdateDTO.latitude,
                travelJournalContentUpdateDTO.longitude,
                images.value,
                deleteImages.value
            )
            _event.emit(Event.ShowSelectPlace(dto, emptyList()))
        }
    }

    fun updatePlace(dto: SelectPlaceDTO) {
        viewModelScope.launch {
            _placeId.emit(dto.placeId)
            _placeName.emit(dto.name)
        }
    }

    sealed class Event {
        data class ShowDailyDatePicker(
            val currentDate: LocalDate?,
            val minDateMillis: Long,
            val maxDateMillis: Long,
        ) : Event()

        data class ShowSelectPlace(
            val updateDTO: TravelJournalContentUpdateDTO,
            val placePredictionDTO : List<PlacePredictionDTO>
        ): Event()

        object ClearDatePickerDialog : Event()
        object NoContentData : Event()
        object NoImagesData : Event()
        data class SuccessUpdate(
            val travelJournalId: Long
        ) : Event()
    }
    companion object {
        private const val IMAGE_EXTENSION_WEBP = ".webp"
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
