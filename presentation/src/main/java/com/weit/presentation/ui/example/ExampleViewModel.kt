package com.weit.presentation.ui.example

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.example.GetUserUseCase
import com.weit.domain.usecase.image.GetImagesUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getImagesUseCase: GetImagesUseCase,
) : ViewModel() {

    val query = MutableStateFlow("")

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> get() = _images

    private val _lastSearchedUser = MutableStateFlow("")
    val lastSearchedUser: StateFlow<String> get() = _lastSearchedUser

    private val _errorEvent = MutableEventFlow<Throwable>()
    val errorEvent = _errorEvent.asEventFlow()

    private var searchJob: Job = Job().apply {
        cancel()
    }

    fun getImages() {
        viewModelScope.launch {
            val result = getImagesUseCase()
            if (result.isSuccess) {
                _images.emit(result.getOrThrow())
            } else {
                _errorEvent.emit(result.exceptionOrNull() ?: Exception())
            }
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
