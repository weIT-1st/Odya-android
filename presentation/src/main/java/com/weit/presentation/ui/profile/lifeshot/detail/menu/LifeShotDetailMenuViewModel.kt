package com.weit.presentation.ui.profile.lifeshot.detail.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.usecase.image.DeleteLifeShotUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class LifeShotDetailMenuViewModel @AssistedInject constructor(
    private val deleteLifeShotUseCase: DeleteLifeShotUseCase,
    @Assisted private val imageId: Long,

    ) : ViewModel() {

    @AssistedFactory
    interface LifeShotFactory {
        fun create(imageId: Long): LifeShotDetailMenuViewModel
    }

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    fun deleteLifeShot() {
        viewModelScope.launch {
            val result = deleteLifeShotUseCase(imageId)
            if (result.isSuccess) {
                _event.emit(Event.OnDeleteLifeShotSuccess)
            } else {
                // TODO 에러 처리
            }
        }
    }

    sealed class Event {

        object OnDeleteLifeShotSuccess : Event()
    }

    companion object {
        fun provideFactory(
            assistedFactory: LifeShotFactory,
            imageId: Long,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(imageId) as T
            }
        }
    }
}
