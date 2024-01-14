package com.weit.presentation.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayout
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotPlaceDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.post.FeedPostTopicAdapter
import com.weit.presentation.ui.feed.post.FeedPostViewModel
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerFragmentDirections
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerViewModel
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        FeedPostViewModel.provideFactory(viewModelFactory, args.feedImages?.toList() ?: emptyList(),args.feedId)
    }

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.selectVisibility(Visibility.fromPosition(tab.position))
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm =viewModel
        binding.vpFeedPost.adapter = feedImageAdapter
        binding.tlFeedPostVisibility.addOnTabSelectedListener(tabSelectedListener)
        initTopics()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                findNavController().currentBackStackEntry?.savedStateHandle?.getStateFlow<SelectLifeShotPlaceDTO?>(
                    "place",
                    null
                )?.collect { place ->
                    if (place != null) {
                        viewModel.selectFeedPlace(place)
                    }
                }
            }
        }
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
        binding.btnFeedPostPlace.setOnClickListener {
                val direction = FeedPostFragmentDirections.actionFragmentFeedPostToFeedSelectPlaceFragment()
                findNavController().navigate(direction)
        }
    }

    private fun initTopics(){
        binding.rvTopic.run{
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
            viewModel.feed.collectLatest { feed ->
                binding.tvFeedPostTitle.text = feed?.travelJournal?.title
                //TODO 장소id변경
                feed?.visibility?.let { binding.tlFeedPostVisibility.getTabAt(Visibility.valueOf(it).position)?.select() }
            }
        }
    }

    private fun handleEvent(event: FeedPostViewModel.Event) {
        when (event) {
            is FeedPostViewModel.Event.FeedPostSuccess -> {
                val action = FeedPostFragmentDirections.actionFragmentFeedPostToFragmentFeed()
                findNavController().navigate(action)
            }
            is FeedPostViewModel.Event.FeedUpdateSuccess -> {
                val action = FeedPostFragmentDirections.actionFragmentFeedPostToFragmentFeed()
                findNavController().navigate(action)
            }
            is FeedPostViewModel.Event.OnChangeTopics -> {
                feedPostTopicAdapter.submitList(event.topics)
            }
            is FeedPostViewModel.Event.OnSelectPlaceCompleted -> {
                binding.btnFeedPostPlace.text = event.placeName
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.tlFeedPostVisibility.removeOnTabSelectedListener(tabSelectedListener)
        binding.rvTopic.adapter = null
        super.onDestroyView()
    }
}
