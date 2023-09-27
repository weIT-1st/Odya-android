package com.weit.presentation.ui.map.bottomsheet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import com.weit.domain.usecase.place.GetPlaceImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempBottomSheetViewModel @Inject constructor(
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val getPlaceImageUseCase: GetPlaceImageUseCase,
) : ViewModel() {

    private val _placeImage = MutableStateFlow<Bitmap?>(null)
    val placeImage: StateFlow<Bitmap?> get() = _placeImage

    private val _placeTitle = MutableStateFlow<String>("")
    val placeTitle: StateFlow<String> get() = _placeTitle

    private val _placeAddress = MutableStateFlow<String>("")
    val placeAddress: StateFlow<String> get() = _placeAddress

    fun getPlaceImage(placeId: String) {
        viewModelScope.launch{
            val placeImageByteArray = getPlaceImageUseCase(placeId)
            if (placeImageByteArray != null){
                _placeImage.emit(BitmapFactory.decodeByteArray(placeImageByteArray, 0, placeImageByteArray.size))
            }
        }
    }

    fun getPlaceInform(placeId: String) {
        viewModelScope.launch {
            val result = getPlaceDetailUseCase(placeId)

            if (result.address.isNullOrBlank().not()) {
                _placeAddress.emit(result.address!!)
            }

            if (result.name.isNullOrBlank().not()) {
                _placeTitle.emit(result.name!!)
            }
        }
    }
}
