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
import com.weit.domain.usecase.term.SetTermsUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginConsentDialogViewModel @Inject constructor(
//    private val getAgreedTermsUseCase: GetAgreedTermsUseCase,
    private val getTermContentUseCase: GetTermContentUseCase,
    private val getTermListUseCase: GetTermListUseCase,
    private val setTermsUseCase: SetTermsUseCase,
) : ViewModel() {

    private  val _termTitle = MutableStateFlow<String>("")
    val termTitle : StateFlow<String> get() = _termTitle

    private  val _termContent = MutableStateFlow<String>("")
    val termContent : StateFlow<String> get() = _termContent
    private val _event = MutableEventFlow<LoginConsentDialogViewModel.Event>()
    val event = _event.asEventFlow()

    private var termId :Long = 0

    init {
        getTermList()
        getTermContent()
//        setTerms()
//        getAgreedTerms()
    }

    private fun getTermContent() {
        viewModelScope.launch {
            val result = getTermContentUseCase(termId)
            if (result.isSuccess) {
                val terms = result.getOrThrow()
                _termContent.emit(terms.content)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    private fun getTermList() {
        viewModelScope.launch {
            val result = getTermListUseCase()
            if (result.isSuccess) {
                val terms = result.getOrThrow()
                termId=terms[0].termId
                _termTitle.emit(terms[0].title)
            } else {
                handleError(result.exceptionOrNull() ?: UnKnownException())
                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
            }
        }
    }

    fun onAgree() {
        viewModelScope.launch {
            _event.emit(Event.OnAgreeSuccess)
        }
    }

//    fun updateTerm() {
//        val result = setTermsUseCase(TermIdListInfo(listOf(1,2),listOf(3)))
//            if (result.isSuccess) {
//                Logger.t("MainTest").i("변경 성공성공")
//            } else {
//                handleError(result.exceptionOrNull() ?: UnKnownException())
//                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
//            }
//    }

//    private fun getAgreedTerms() {
//        viewModelScope.launch {
//            val result = getAgreedTermsUseCase()
//            if (result.isSuccess) {
//                val terms = result.getOrThrow()
//                Logger.t("MainTest").i("동의한 목록 ${terms}")
//            } else {
//                handleError(result.exceptionOrNull() ?: UnKnownException())
//                Logger.t("MainTest").i("실패 ${result.exceptionOrNull()?.javaClass?.name}")
//            }
//        }
//    }
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
