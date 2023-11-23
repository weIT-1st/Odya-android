package com.weit.presentation.ui.searchplace.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weit.domain.model.report.ReportReason
import com.weit.domain.model.report.ReviewReportRequestInfo
import com.weit.domain.usecase.report.ReviewReportUseCase
import com.weit.presentation.ui.util.MutableEventFlow
import com.weit.presentation.ui.util.asEventFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewReportViewModel @AssistedInject constructor(
    private val reviewReportReasonUseCase: ReviewReportUseCase,
    @Assisted private val placeReviewId: Long
): ViewModel() {

    private val _reportReason = MutableStateFlow(ReportReason.SPAM)
    val reportReason: StateFlow<ReportReason> get() = _reportReason

    val otherReason = MutableStateFlow("")

    private val _event = MutableEventFlow<Event>()
    val event = _event.asEventFlow()

    @AssistedFactory
    interface ReviewReportContentFactory{
        fun create(placeReviewId: Long): ReviewReportViewModel
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
            val reviewReportRequestInfo = ReviewReportRequestInfo(
                placeReviewId,
                reportReason,
                otherReason
            )

            if (reportReason == ReportReason.OTHER && otherReason.isBlank()) {
                _event.emit(Event.EmptyOtherReason)
            } else {
                val result = reviewReportReasonUseCase(reviewReportRequestInfo)

                if (result.isSuccess){
                    _event.emit(Event.SuccessReportReview)
                } else {

                }
            }
        }
    }

    sealed class Event{
        object EmptyOtherReason: Event()
        object SuccessReportReview: Event()
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
    }
}
