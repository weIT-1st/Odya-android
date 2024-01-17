package com.weit.presentation.ui.feed.detail.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.databinding.BottomSheetFeedMyMenuBinding
import com.weit.presentation.databinding.BottomSheetFeedOtherMenuBinding
import com.weit.presentation.ui.feed.detail.menu.report.FeedReportFragment
import com.weit.presentation.ui.searchplace.report.ReviewReportFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedDetailOtherMenuFragment(val feedId: Long,val nickname: String) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedOtherMenuBinding? = null
    private val binding get() = _binding!!
    private var feedReportFragment: FeedReportFragment? = null

    @Inject
    lateinit var viewModelFactory: FeedDetailOtherMenuViewModel.FeedDetailFactory

    private val viewModel: FeedDetailOtherMenuViewModel by viewModels {
        FeedDetailOtherMenuViewModel.provideFactory(viewModelFactory, feedId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetFeedOtherMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener(){
        binding.tvOtherFeedClose.setOnClickListener {
            dismiss()
        }
        binding.tvOtherFeedReport.setOnClickListener {
            if (feedReportFragment == null){
                feedReportFragment = FeedReportFragment(feedId, nickname)
            }

            if (!feedReportFragment!!.isAdded){
                feedReportFragment!!.show(childFragmentManager, FeedDetailOtherMenuFragment.TAG)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "FeedDetailOtherMenuFragment"
    }
}
