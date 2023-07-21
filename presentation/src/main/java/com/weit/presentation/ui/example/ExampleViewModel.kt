package com.weit.presentation.ui.example

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.usecase.DeleteCoordinateUseCase
import com.weit.domain.usecase.GetCoordinatesUseCase
import com.weit.domain.usecase.InsertCoordinateUseCase
import com.weit.domain.usecase.example.GetUserUseCase
import com.weit.domain.usecase.image.GetImageCoordinatesUseCase
import com.weit.domain.usecase.image.GetImagesUseCase
import com.weit.domain.usecase.image.GetScaledImageBytesByUrisUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getImagesUseCase: GetImagesUseCase,
    private val getScaledImageBytesByUrisUseCase: GetScaledImageBytesByUrisUseCase,
    private val getImageCoordinatesUseCase: GetImageCoordinatesUseCase,
    private val getCoordinatesUseCase: GetCoordinatesUseCase,
    private val insertCoordinateUseCase: InsertCoordinateUseCase,
    private val deleteCoordinateUseCase: DeleteCoordinateUseCase,
) : ViewModel() {

    val query = MutableStateFlow("")

    private val _lastSearchedUser = MutableStateFlow("")
    val lastSearchedUser: StateFlow<String> get() = _lastSearchedUser

    private val _loadImageEvent = MutableEventFlow<ByteArray>()
    val loadImageEvent = _loadImageEvent.asEventFlow()

    private val _errorEvent = MutableEventFlow<Throwable>()
    val errorEvent = _errorEvent.asEventFlow()

    private var searchJob: Job = Job().apply {
        cancel()
    }

    init {
        // getImages()
//        insertCoordinate()
//        getCoordinates()
        // deleteCoordinate()
    }

    // location room database test
    private fun insertCoordinate() {
        val lat: Float = 0.2343f
        val lng: Float = 0.2343f

        viewModelScope.launch {
            for (i in 0..5) {
                insertCoordinateUseCase(lat, lng)
                Thread.sleep(5000)
            }
        }
    }

    private fun getCoordinates() {
        viewModelScope.launch {
            val result = getCoordinatesUseCase(1689925493106, 1689925518186)
            for (location in result) {
                Log.d("LocationInfo", "lat ${location.lat} lng ${location.lng}")
            }
        }
    }

    private fun deleteCoordinate() {
        viewModelScope.launch {
            deleteCoordinateUseCase(1)
        }
    }

    private fun getImageCoordinates(uri: String) {
        viewModelScope.launch {
            val result = getImageCoordinatesUseCase(uri)
            Log.d("LatLong", "lat ${result?.latitude} lng ${result?.longitude}")
        }
    }
    private fun getImages() {
        viewModelScope.launch {
            val result = getImagesUseCase()
            if (result.isSuccess) {
                val uris = result.getOrThrow().subList(0, 100)
                // Test to get coordinates
                for (x in uris) {
                    Log.d("LatLong", x)
                    getImageCoordinates(x)
                }
                convertUrisToImageBytes(uris)
            } else {
                _errorEvent.emit(result.exceptionOrNull() ?: Exception())
            }
        }
    }
    private fun convertUrisToImageBytes(uris: List<String>) {
        viewModelScope.launch {
            val millis = measureTimeMillis {
                val list = getScaledImageBytesByUrisUseCase(uris)
                Logger.t("MainTest").i(list.sumOf { it.size }.toString())
                list.firstOrNull()?.let {
                    _loadImageEvent.emit(it)
                }
            }
            Logger.t("MainTest").i("$millis")
        }
    }

    @MainThread
    fun onSearch() {
        if (searchJob.isCompleted) {
            searchUser(query.value)
        }
    }

    private fun searchUser(name: String) {
        searchJob = viewModelScope.launch {
            val result = getUserUseCase(name)
            if (result.isSuccess) {
                val user = result.getOrThrow()
                _lastSearchedUser.emit(user.name)
            } else {
                _errorEvent.emit(result.exceptionOrNull() ?: Exception())
            }
        }
    }
}
