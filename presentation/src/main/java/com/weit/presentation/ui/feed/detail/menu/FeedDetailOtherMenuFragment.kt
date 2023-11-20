package com.weit.presentation.ui.feed.detail.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.databinding.BottomSheetFeedMyMenuBinding
import com.weit.presentation.databinding.BottomSheetFeedOtherMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedDetailOtherMenuFragment(val feedId: Long) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedOtherMenuBinding? = null
    private val binding get() = _binding!!

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

        binding.tvMyFeedClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "FeedDetailOtherMenuFragment"
    }
}
