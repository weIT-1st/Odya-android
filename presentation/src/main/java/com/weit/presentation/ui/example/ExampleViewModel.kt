package com.weit.presentation.ui.example

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.usecase.example.GetUserUseCase
import com.weit.domain.usecase.image.GetImageBytesByUrisUseCase
import com.weit.domain.usecase.image.GetImagesUseCase
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
    private val getImageBytesByUrisUseCase: GetImageBytesByUrisUseCase,
) : ViewModel() {

    val query = MutableStateFlow("")

    private val _lastSearchedUser = MutableStateFlow("")
    val lastSearchedUser: StateFlow<String> get() = _lastSearchedUser

    private val _errorEvent = MutableEventFlow<Throwable>()
    val errorEvent = _errorEvent.asEventFlow()

    private var searchJob: Job = Job().apply {
        cancel()
    }

    init {
        getImages()
    }

    private fun getImages() {
        viewModelScope.launch {
            val result = getImagesUseCase()
            if (result.isSuccess) {
                val uris = result.getOrThrow().subList(0, 100)
                convertUrisToImageBytes(uris)
            } else {
                _errorEvent.emit(result.exceptionOrNull() ?: Exception())
            }
        }
    }

    private fun convertUrisToImageBytes(uris: List<String>) {
        viewModelScope.launch {
            val millis = measureTimeMillis {
                getImageBytesByUrisUseCase(uris)
            }
            // 10장 2초, 100장 15초
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
