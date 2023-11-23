package com.weit.presentation.ui.searchplace.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.weit.domain.model.report.ReportReason
import com.weit.presentation.R
import com.weit.presentation.databinding.DialogReviewReportBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReviewReportFragment(
    private val placeReviewId: Long
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

        binding.radioGroupReviewReport.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.radio_btn_review_report_spam -> viewModel.setReportReason(ReportReason.SPAM)
                R.id.radio_btn_review_report_pornography -> viewModel.setReportReason(ReportReason.PORNOGRAPHY)
                R.id.radio_btn_review_report_swear_word -> viewModel.setReportReason(ReportReason.SWEAR_WORD)
                R.id.radio_btn_review_report_over_post -> viewModel.setReportReason(ReportReason.OVER_POST)
                R.id.radio_btn_review_report_info_leak -> viewModel.setReportReason(ReportReason.INFO_LEAK)
                else -> viewModel.setReportReason(ReportReason.OTHER)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
