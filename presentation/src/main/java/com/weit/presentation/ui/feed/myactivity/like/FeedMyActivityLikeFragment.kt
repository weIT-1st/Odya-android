package com.weit.presentation.ui.feed.myactivity.like

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentTabFeedLikeBinding
import com.weit.presentation.databinding.FragmentTabFeedPostBinding
import com.weit.presentation.databinding.FragmentTabPlaceCommunityBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.myactivity.FeedMyActivityImageAdapter
import com.weit.presentation.ui.feed.myactivity.post.FeedMyActivityPostFragmentDirections
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedMyActivityLikeFragment() : BaseFragment<FragmentTabFeedLikeBinding>(
    FragmentTabFeedLikeBinding::inflate,
) {
    private val viewModel: FeedMyActivityLikeViewModel by viewModels()
    private val imageAdapter = FeedMyActivityImageAdapter{
            communityId -> navigateFeedDetail(communityId)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTabFeedLike.run {
            addOnScrollListener(infinityScrollListener)
            adapter = imageAdapter
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextImages()
            }
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.postImages.collectLatest { images ->
                imageAdapter.submitList(images)
            }
        }
    }

    private fun navigateFeedDetail(feedId: Long) {
        //TODO 닉네임 전달
        val action = FeedMyActivityPostFragmentDirections.actionFeedMyActivityPostFragmentToFragmentFeedDetail(feedId,null)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.rvTabFeedLike.removeOnScrollListener(infinityScrollListener)
        binding.rvTabFeedLike.adapter = null
        super.onDestroyView()
    }
}
