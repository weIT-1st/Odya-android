package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.weit.domain.model.topic.TopicDetail
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.post.FeedImageAdapter
import com.weit.presentation.ui.feed.post.FeedPostTopicAdapter
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
    private val feedPostTopicAdapter = FeedPostTopicAdapter(
        clickTopic = { topicId ->  viewModel.selectTopic(topicId) }
    )

    @Inject
    lateinit var viewModelFactory: FeedPostViewModel.FeedPostFactory

    private val viewModel: FeedPostViewModel by viewModels {
        FeedPostViewModel.provideFactory(viewModelFactory,args.feedImages)
    }

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm =viewModel
        binding.vpFeedPost.adapter = feedImageAdapter
    }

    override fun initListener() {
        super.initListener()
        binding.tbFeedPost.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_iamge_update) {
                viewModel.onUpdatePictures(pickImageUseCase)
            }
            true
        }
        binding.tbFeedPost.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initTopics(topics: List<TopicDetail>?){
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }

        feedPostTopicAdapter.submitList(topics)

        binding.rvTopic.run{
            layoutManager = flexboxLayoutManager
            adapter = feedPostTopicAdapter
            setHasFixedSize(false)
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.imageList.collectLatest { images ->
                feedImageAdapter.submitList(images)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.topicList.collectLatest { topics ->
                initTopics(topics)
            }
        }
    }

    private fun handleEvent(event: FeedPostViewModel.Event) {
        when (event) {
            is FeedPostViewModel.Event.FeedPostSuccess -> {
                findNavController().popBackStack()
            }

            else -> {}
        }
    }
}
