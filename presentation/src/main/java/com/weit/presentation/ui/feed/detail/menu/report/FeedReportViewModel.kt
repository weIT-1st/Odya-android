package com.weit.presentation.ui.feed.detail.menu.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.exception.InvalidRequestException
import com.weit.domain.model.exception.InvalidTokenException
import com.weit.domain.model.exception.NotExistPlaceReviewException
import com.weit.domain.model.exception.RequestResourceAlreadyExistsException
import com.weit.domain.model.report.CommunityReportRequestInfo
import com.weit.domain.model.report.ReportReason
import com.weit.domain.model.report.ReviewReportRequestInfo
import com.weit.domain.usecase.report.CommunityReportUseCase
import com.weit.domain.usecase.report.ReviewReportUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedReportViewModel @AssistedInject constructor(
    private val communityReportUseCase: CommunityReportUseCase,
    @Assisted private val placeReviewId: Long
): ViewModel() {

    private val _reportReason = MutableStateFlow(ReportReason.SPAM)
    val reportReason: StateFlow<ReportReason> get() = _reportReason

    val otherReason = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface ReviewReportContentFactory{
        fun create(placeReviewId: Long): FeedReportViewModel
    }

    fun setReportReason(reason: ReportReason){
        viewModelScope.launch {
            _reportReason.emit(reason)
        }
    }

    fun reportReview(){
        viewModelScope.launch{
            val reportReason = reportReason.value
            val otherReason = otherReason.value
            val info = if (reportReason == ReportReason.OTHER) {
                CommunityReportRequestInfo(
                    placeReviewId,
                    reportReason,
                    otherReason
                )
            } else {
                CommunityReportRequestInfo(
                    placeReviewId,
                    reportReason,
                    null
                )
            }
            if (reportReason == ReportReason.OTHER && info.otherReason.isNullOrBlank()){
                _event.emit(Event.EmptyOtherReason)
            } else if (reportReason == ReportReason.OTHER && info.otherReason!!.length > MAX_OTHER_REASON_LENGTH){
                _event.emit(Event.TooLongOtherReason)
            } else {
                val result = communityReportUseCase(info)

                if (result.isSuccess){
                    _event.emit(Event.SuccessReviewReport)
                } else {
                    handleReportException(result.exceptionOrNull() ?: UnknownError())
                }
            }
        }
    }

    private suspend fun handleReportException(error: Throwable){
        when (error){
            is NotExistPlaceReviewException -> _event.emit(Event.NotExistReview)
            is InvalidRequestException -> _event.emit(Event.MyselfReport)
            is RequestResourceAlreadyExistsException -> _event.emit(Event.DuplicateReport)
            is InvalidTokenException -> _event.emit(Event.ForbiddenException)
            else -> _event.emit(Event.UnknownException)
        }
    }


    sealed class Event{
        object EmptyOtherReason: Event()
        object TooLongOtherReason: Event()
        object NotExistReview: Event()
        object MyselfReport: Event()
        object DuplicateReport: Event()
        object ForbiddenException : Event()
        object UnknownException : Event()
        object SuccessReviewReport: Event()
    }

    companion object{
        fun provideFactory(
            assistedFactory: ReviewReportContentFactory,
            placeReviewId: Long
        ): ViewModelProvider.Factory = object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(placeReviewId) as T
            }
        }

        private const val MAX_OTHER_REASON_LENGTH: Int = 20
    }
}
