package com.weit.presentation.ui.profile.preferences.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class SettingDetailViewModel @AssistedInject constructor(
    @Assisted private val detailType : String
): ViewModel() {
    @AssistedFactory
    interface SettingDetailFactory {
        fun create(detailType: String): SettingDetailViewModel
    }

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    init {
        initDetailType()
    }

    private fun initDetailType() {
        viewModelScope.launch {
            when (detailType) {
                "PRIVACY_POLICY" -> {
                    _event.emit(Event.PrivacyPolicy(""))
                }
                "TERMS_OF_USE" -> {
                    _event.emit(Event.TermsOfUse(""))
                }
                "OPEN_SOURCE" -> {
                    _event.emit(Event.OpenSource(""))
                }
            }
        }
    }
    sealed class Event {
        data class PrivacyPolicy(val a : String) : Event()
        data class TermsOfUse(val a : String) : Event()
        data class OpenSource(val a : String) : Event()
    }

    companion object{
        fun provideFactory(
            assistedFactory: SettingDetailFactory,
            detailType: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(detailType) as T
            }
        }
    }
}
