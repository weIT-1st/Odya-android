package com.weit.presentation.ui.feed.myactivity.comment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentTabFeedCommentBinding
import com.weit.presentation.databinding.FragmentTabPlaceCommunityBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.myactivity.FeedMyActivityFragmentDirections
import com.weit.presentation.ui.feed.myactivity.FeedMyActivityImageAdapter
import com.weit.presentation.ui.feed.myactivity.like.FeedMyActivityLikeViewModel
import com.weit.presentation.ui.feed.myactivity.post.FeedMyActivityPostFragmentDirections
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedMyActivityCommentFragment(val nickname: String) : BaseFragment<FragmentTabFeedCommentBinding>(
    FragmentTabFeedCommentBinding::inflate,
) {
    private val viewModel: FeedMyActivityCommentViewModel by viewModels()
    private val myActivityCommentAdapter = FeedMyActivityCommentAdapter{
            communityId -> navigateFeedDetail(communityId)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTabFeedComment.run {
            addOnScrollListener(infinityScrollListener)
            adapter = myActivityCommentAdapter
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextComments()
            }
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.postContents.collectLatest { contents ->
                myActivityCommentAdapter.submitList(contents)
            }
        }
    }

    private fun navigateFeedDetail(feedId: Long) {
        val action =
            FeedMyActivityFragmentDirections.actionFragmentFeedMyActivityToFragmentFeedDetail(
                feedId,
                nickname
            )
        findNavController().navigate(action)
    }
    override fun onDestroyView() {
        binding.rvTabFeedComment.removeOnScrollListener(infinityScrollListener)
        binding.rvTabFeedComment.adapter = null
        super.onDestroyView()
    }
}

