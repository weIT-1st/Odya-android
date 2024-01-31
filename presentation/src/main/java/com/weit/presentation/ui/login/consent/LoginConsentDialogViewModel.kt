package com.weit.presentation.ui.login.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NotExistTermIdException
import com.weit.domain.model.exception.UnKnownException
import com.weit.domain.usecase.term.GetTermContentUseCase
import com.weit.domain.usecase.term.GetTermListUseCase
import com.weit.domain.usecase.userinfo.SetTermIdListUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginConsentDialogViewModel @Inject constructor(
    private val getTermContentUseCase: GetTermContentUseCase,
    private val getTermListUseCase: GetTermListUseCase,
    private val setTermIdListUsecase: SetTermIdListUseCase,
) : ViewModel() {

    private  val _termTitle = MutableStateFlow<String>("")
    val termTitle : StateFlow<String> get() = _termTitle

    private  val _termContent = MutableStateFlow<String>("")
    val termContent : StateFlow<String> get() = _termContent
    private val _event = MutableEventFlow<LoginConsentDialogViewModel.Event>()
    val event = _event.asEventFlow()

    private val termIdList  = mutableSetOf<String>()

    init {
        getTermList()
    }

    private fun getTermList() {
        viewModelScope.launch {
            val result = getTermListUseCase()
            if (result.isSuccess) {
                val terms = result.getOrThrow()
                termIdList.add(terms[0].termId.toString())
                _termTitle.emit(terms[0].title)
                getTermContent()
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun getTermContent() {
        viewModelScope.launch {
            val result = getTermContentUseCase(termIdList.first().toLong())
            if (result.isSuccess) {
                val terms = result.getOrThrow()
                _termContent.emit(terms.content)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }


    fun onAgree() {
        viewModelScope.launch {
            setTermIdListUsecase(termIdList)
            _event.emit(Event.OnAgreeSuccess)
        }
    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is InvalidRequestException -> _event.emit(Event.InvalidRequestException)
            is InvalidTokenException -> _event.emit(Event.InvalidTokenException)
            is NotExistTermIdException -> _event.emit(Event.NotHavePermissionException)
            else -> _event.emit(Event.UnknownException)
        }
    }

    sealed class Event {
        object OnAgreeSuccess : Event()
        object InvalidRequestException : Event()
        object InvalidTokenException : Event()
        object NotHavePermissionException : Event()
        object UnknownException : Event()
    }
}
