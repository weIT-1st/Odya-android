package com.weit.presentation.ui.map.bottomsheet

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPhotoResponse
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.weit.domain.usecase.place.GetPlaceDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class TempBottomSheetViewModel @Inject constructor(
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
) : ViewModel() {

    private val _placeImage = MutableStateFlow<Bitmap?>(null)
    val placeImage: StateFlow<Bitmap?> get() = _placeImage

    private val _placeTitle = MutableStateFlow<String>("")
    val placeTitle : StateFlow<String> get() = _placeTitle

    private val _placeAddress = MutableStateFlow<String>("")
    val placeAddress : StateFlow<String> get() = _placeAddress

    fun getPlaceImage(placeId: String, placesClient: PlacesClient) {
        val fields = listOf(Field.PHOTO_METADATAS)
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
        Log.d("getPlaceImage", "start")

        placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                val metadata = place.photoMetadatas
                if (metadata == null || metadata.isEmpty()) {
                    Log.d("getPlaceImage", "No Photo metadata")
                    return@addOnSuccessListener
                }
                val photoMetadata = metadata.first()
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build()

                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        viewModelScope.launch {
                            val bitmap = fetchPhotoResponse.bitmap
                            _placeImage.emit(bitmap)
                            Log.d("getPlaceImang", "Success")
                        }
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("getPlaceImage", "Place not fount: " + exception.message)
                        }
                    }
            }.addOnFailureListener {
                Log.e("getPlaceImage", "fail : " + it.message)
            }
    }

    fun getPlaceInform(placeId: String){
        viewModelScope.launch {
            val result = getPlaceDetailUseCase(placeId)

            if (result.address.isNullOrBlank().not()){
                _placeAddress.emit(result.address!!)
            }

            if (result.name.isNullOrBlank().not()){
                _placeTitle.emit(result.name!!)
            }
        }
    }
}
