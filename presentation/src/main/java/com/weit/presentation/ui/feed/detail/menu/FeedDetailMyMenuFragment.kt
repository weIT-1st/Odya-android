package com.weit.presentation.ui.feed.detail.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetFeedMyMenuBinding
import com.weit.presentation.ui.feed.FeedDetailFragmentArgs
import com.weit.presentation.ui.feed.FeedDetailFragmentDirections
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedDetailMyMenuFragment(val feedId: Long) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFeedMyMenuBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: FeedDetailMyMenuViewModel.FeedDetailFactory

    private val viewModel: FeedDetailMyMenuViewModel by viewModels {
        FeedDetailMyMenuViewModel.provideFactory(viewModelFactory, feedId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetFeedMyMenuBinding.inflate(inflater, container, false)
        initCollector()

        binding.tvMyFeedUpdate.setOnClickListener {
            dismiss()
            val action = FeedDetailFragmentDirections.actionFragmentFeedDetailToFragmentFeedPost(
                emptyArray() ,feedId)
            findNavController().navigate(action)
        }
        binding.tvMyFeedDelete.setOnClickListener {
            viewModel.deleteFeed()
        }
        binding.tvMyFeedClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: FeedDetailMyMenuViewModel.Event) {
        when (event) {
            is FeedDetailMyMenuViewModel.Event.FeedDeleteSuccess -> {
                dismiss()
                val action = FeedDetailMyMenuFragmentDirections.actionFragmentFeedDetailMyMenuToFragmentFeed()
                findNavController().navigate(action)
            }
            else -> {}
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "FeedDetailMyMenuDialog"
    }
}
