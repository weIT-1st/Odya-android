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
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.ui.base.BaseFragment
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
        selectTopic = { topicId->
            viewModel.selectTopic(topicId) }
    )

    @Inject
    lateinit var viewModelFactory: FeedPostViewModel.FeedPostFactory

    private val viewModel: FeedPostViewModel by viewModels {
        FeedPostViewModel.provideFactory(viewModelFactory, args.feedImages?.toList() ?: emptyList())
    }

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val flexboxLayoutManager: FlexboxLayoutManager by lazy {
        FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm =viewModel
        binding.vpFeedPost.adapter = feedImageAdapter
        initTopics()
    }

    override fun initListener() {
        super.initListener()
        binding.tbFeedPost.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_image_update) {
                viewModel.onUpdatePictures(pickImageUseCase)
            }
            true
        }
        binding.tbFeedPost.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initTopics(){
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
    }

    private fun handleEvent(event: FeedPostViewModel.Event) {
        when (event) {
            is FeedPostViewModel.Event.FeedPostSuccess -> {
                val action = FeedPostFragmentDirections.actionFragmentFeedPostToFragmentFeed()
                findNavController().navigate(action)
            }
            is FeedPostViewModel.Event.OnChangeTopics -> {
                feedPostTopicAdapter.submitList(event.topics)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvTopic.adapter = null
        super.onDestroyView()
    }
}
