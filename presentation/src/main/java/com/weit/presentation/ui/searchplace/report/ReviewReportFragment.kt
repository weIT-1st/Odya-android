package com.weit.presentation.ui.searchplace.report

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
import com.weit.presentation.databinding.DialogReviewReportBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ReviewReportFragment(
    private val placeReviewId: Long,
    private val writer: String
): DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ReviewReportViewModel.ReviewReportContentFactory

    private val viewModel: ReviewReportViewModel by viewModels{
        ReviewReportViewModel.provideFactory(viewModelFactory, placeReviewId)
    }

    private var _binding: DialogReviewReportBinding? = null
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
        _binding = DialogReviewReportBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvReviewReportMe.text = writer

        binding.radioGroupReviewReport.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.radio_btn_review_report_spam -> viewModel.setReportReason(ReportReason.SPAM)
                R.id.radio_btn_review_report_pornography -> viewModel.setReportReason(ReportReason.PORNOGRAPHY)
                R.id.radio_btn_review_report_swear_word -> viewModel.setReportReason(ReportReason.SWEAR_WORD)
                R.id.radio_btn_review_report_over_post -> viewModel.setReportReason(ReportReason.OVER_POST)
                R.id.radio_btn_review_report_copyright_violation -> viewModel.setReportReason(ReportReason.COPYRIGHT_VIOLATION)
                R.id.radio_btn_review_report_info_leak -> viewModel.setReportReason(ReportReason.INFO_LEAK)
                else -> viewModel.setReportReason(ReportReason.OTHER)
            }
        }

        binding.btnReviewReport.setOnClickListener {
            viewModel.reportReview()
        }

        binding.btnReivewReportX.setOnClickListener {
            dismiss()
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

    private fun handleEvent(event: ReviewReportViewModel.Event){
        when(event){
            ReviewReportViewModel.Event.SuccessReviewReport -> {}
            ReviewReportViewModel.Event.EmptyOtherReason -> {
                sendSnackBar("기타사유를 입력하세요")
            }
            ReviewReportViewModel.Event.TooLongOtherReason -> {}
            ReviewReportViewModel.Event.DuplicateReport -> {
                sendSnackBar("이미 신고한 한줄 리뷰 입니다.")
            }
            ReviewReportViewModel.Event.ForbiddenException -> {
                sendSnackBar("로그인 상태를 확인하세요")
            }
            ReviewReportViewModel.Event.MyselfReport -> {
                sendSnackBar("본인이 쓴 리뷰는 신고 할수 없습니다.")
            }
            ReviewReportViewModel.Event.NotExistReview -> {
                sendSnackBar("존재하지 않는 한줄 리뷰 입니다.")
            }
            ReviewReportViewModel.Event.UnknownException -> {
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
