package com.weit.data.repository.image

import android.content.Context
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weit.domain.repository.image.GalleryRepository
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    @ActivityContext private val context: Context,
) : GalleryRepository {

    private val imagesEvent = MutableSharedFlow<List<String>>()

    private val pickImagesResult = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_SELECT_IMAGE_COUNT)) { uris ->
        CoroutineScope(Dispatchers.Default).launch {
            imagesEvent.emit(uris.map { it.toString() })
        }
    }

    override suspend fun pickImages(): List<String> {
        pickImagesResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        return imagesEvent.first()
    }

    companion object {
        private const val MAX_SELECT_IMAGE_COUNT = 15
    }
}
