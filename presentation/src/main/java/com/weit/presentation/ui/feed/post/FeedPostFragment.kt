package com.weit.presentation.ui.feed

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.CommentDialogViewModel
import com.weit.presentation.ui.feed.detail.FeedCommentAdapter
import com.weit.presentation.ui.feed.post.FeedImageAdapter
import com.weit.presentation.ui.feed.post.FeedPostViewModel
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedPostFragment : BaseFragment<FragmentFeedPostBinding>(
    FragmentFeedPostBinding::inflate,
) {

    private val args: FeedPostFragmentArgs by navArgs()
    private val feedImageAdapter = FeedImageAdapter()

    @Inject
    lateinit var viewModelFactory: FeedPostViewModel.FeedPostFactory

    private val viewModel: FeedPostViewModel by viewModels {
        FeedPostViewModel.provideFactory(viewModelFactory,args.feedImages.images)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm =viewModel

        feedImageAdapter.submitList(args.feedImages.images)
        binding.vpFeedPost.adapter = feedImageAdapter
        binding.vpFeedPost.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: FeedPostViewModel.Event) {
        when (event) {
            is FeedPostViewModel.Event.initTopics -> {
                event.topics.forEach {
                    val chip = Chip(requireContext())
                    chip.text = it.topicWord
                    chip.textSize = 16F
                    binding.cgFeedPostTopic.addView(chip)

                    chip.setOnClickListener {
                        chip.setTextColor(Color.BLACK)
                        chip.setChipBackgroundColorResource(R.color.primary)

                        viewModel.selectTopic(chip.text.toString())
                    }
                }
            }
            is FeedPostViewModel.Event.FeedPostSuccess -> {
                findNavController().popBackStack()
            }

            else -> {}
        }
    }
}
