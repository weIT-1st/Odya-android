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
import com.google.android.material.tabs.TabLayout
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.model.Visibility
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
        binding.tlFeedPostVisibility.addOnTabSelectedListener(tabSelectedListener)
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
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.feed.collectLatest { feed ->
                //여행일지 제목 변경
                binding.tvFeedPostTitle.text = feed?.travelJournal?.title
                //장소id변경
                //토픽 변경

                //공개여부 변경
                when (feed?.visibility) {
                    Visibility.PUBLIC.name -> binding.tlFeedPostVisibility.getTabAt(0)?.select()
                    Visibility.FRIEND_ONLY.name -> binding.tlFeedPostVisibility.getTabAt(1)?.select()
                    Visibility.PRIVATE.name -> binding.tlFeedPostVisibility.getTabAt(2)?.select()
                }
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
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.tlFeedPostVisibility.removeOnTabSelectedListener(tabSelectedListener)
        binding.rvTopic.adapter = null
        super.onDestroyView()
    }
}
