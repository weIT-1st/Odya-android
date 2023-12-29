package com.weit.presentation.ui.feed.detail.menu.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.weit.domain.model.report.ReportReason
import com.weit.presentation.R
import com.weit.presentation.databinding.DialogFeedReportBinding
import com.weit.presentation.databinding.DialogReviewReportBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedReportFragment(
    private val feedId: Long,
    private val writer: String
): DialogFragment() {

    @Inject
    lateinit var viewModelFactory: FeedReportViewModel.ReviewReportContentFactory

    private val viewModel: FeedReportViewModel by viewModels{
        FeedReportViewModel.provideFactory(viewModelFactory, feedId)
    }

    private var _binding: DialogFeedReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFeedReportBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvFeedReportMe.text = writer

        binding.radioGroupFeedReport.setOnCheckedChangeListener { _, checkedId ->
            val reportReason = when(checkedId){
                R.id.radio_btn_feed_report_spam -> ReportReason.SPAM
                R.id.radio_btn_feed_report_pornography -> ReportReason.PORNOGRAPHY
                R.id.radio_btn_feed_report_swear_word -> ReportReason.SWEAR_WORD
                R.id.radio_btn_feed_report_over_post -> ReportReason.OVER_POST
                R.id.radio_btn_feed_report_copyright_violation -> ReportReason.COPYRIGHT_VIOLATION
                R.id.radio_btn_feed_report_info_leak -> ReportReason.INFO_LEAK
                else -> ReportReason.OTHER
            }
            viewModel.setReportReason(reportReason)
        }

        binding.btnFeedReport.setOnClickListener {
            viewModel.reportReview()
        }

        binding.btnFeedReportX.setOnClickListener {
            dismiss()
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.reportReason.collectLatest { reportReason ->
                val reportReasonId = when(reportReason){
                    ReportReason.SPAM-> R.id.radio_btn_feed_report_spam
                    ReportReason.PORNOGRAPHY -> R.id.radio_btn_feed_report_pornography
                    ReportReason.SWEAR_WORD -> R.id.radio_btn_feed_report_swear_word
                    ReportReason.OVER_POST -> R.id.radio_btn_feed_report_over_post
                    ReportReason.COPYRIGHT_VIOLATION -> R.id.radio_btn_feed_report_copyright_violation
                    ReportReason.INFO_LEAK -> R.id.radio_btn_feed_report_info_leak
                    else -> R.id.et_feed_report_other_reason
                }
                binding.radioGroupFeedReport.check(reportReasonId)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleEvent(event: FeedReportViewModel.Event){
        when(event){
            FeedReportViewModel.Event.SuccessReviewReport -> {
                dismiss()
            }
            FeedReportViewModel.Event.EmptyOtherReason -> {
                sendSnackBar("기타사유를 입력하세요")
            }
            FeedReportViewModel.Event.TooLongOtherReason -> {}
            FeedReportViewModel.Event.DuplicateReport -> {
                sendSnackBar("이미 신고한 한줄 리뷰 입니다.")
            }
            FeedReportViewModel.Event.ForbiddenException -> {
                sendSnackBar("로그인 상태를 확인하세요")
            }
            FeedReportViewModel.Event.MyselfReport -> {
                sendSnackBar("본인이 쓴 리뷰는 신고 할수 없습니다.")
            }
            FeedReportViewModel.Event.NotExistReview -> {
                sendSnackBar("존재하지 않는 한줄 리뷰 입니다.")
            }
            FeedReportViewModel.Event.UnknownException -> {
                sendSnackBar("알수 없는 이유로 한줄리뷰신고가 실패 했습니다.")
            }
        }
    }

    private fun sendSnackBar(
        message: String,
        @IntRange(from = -2) length: Int = Snackbar.LENGTH_SHORT,
    ) {
        Snackbar.make(
            binding.root,
            message,
            length,
        ).show()
    }
}
