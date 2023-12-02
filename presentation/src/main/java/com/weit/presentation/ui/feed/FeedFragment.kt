package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.orhanobut.logger.Logger
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.databinding.FragmentFeedBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(
    FragmentFeedBinding::inflate,
) {

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter = FeedAdapter(
        navigateTravelLog = { travelLogId -> navigateTravelLog(travelLogId) },
        navigateFeedDetail = { feedId -> navigateFeedDetail(feedId) },
        onFollowChanged = { communityId -> viewModel.onFollowStateChange(communityId) },
        onLikeChanged = { communityId -> viewModel.onLikeStateChange(communityId)},
        scrollListener = { viewModel.onNextFriends() }
        )
    private val topicAdapter = FavoriteTopicAdapter(
        selectTopic = { topicId, position ->
            viewModel.selectFeedTopic(topicId, position)
            binding.btnFeedSortAll.isChecked = false
            binding.btnFeedSortFriend.isChecked = false
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initTopicRecyclerView()
        initCommunityRecyclerView()
    }

    override fun initListener() {
        binding.btnFeedWrite.setOnClickListener {
            viewModel.selectPictures(pickImageUseCase)
        }
        binding.btnFeedSortFriend.setOnClickListener {
            viewModel.selectFeedFriend()
        }
        binding.btnFeedSortAll.setOnClickListener {
            viewModel.selectFeedAll()
        }
        binding.tvFeedUser.setOnClickListener {
            val action = FeedFragmentDirections.actionFragmentFeedToFeedMyActivityFragment()
             findNavController().navigate(action)

        }
    }

    private fun initTopicRecyclerView() {
        binding.rvTopic.adapter = topicAdapter
    }
    private fun initCommunityRecyclerView() {
        binding.rvCommunity.run{
            addOnScrollListener(infinityScrollListener)
            adapter = feedAdapter
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextFeeds()
            }
        }
    }
    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.feed.collectLatest { feeds ->
                feedAdapter.submitList(feeds)
            }
        }
    }

    override fun initListener() {
        binding.btnFeedWrite.setOnClickListener {
            viewModel.onSelectPictures(pickImageUseCase)
        }
    }


    private fun navigateTravelLog(travelLogId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentTravellog(travelLogId)
        findNavController().navigate(action)
    }

    private fun navigateFeedDetail(feedId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentFeedDetail(feedId)
        findNavController().navigate(action)
    }

    private fun navigateFeedPost(uris: List<String>) {
        val action = FeedFragmentDirections.actionFragmentFeedToFeedPostFragment(uris.toTypedArray(),-1)
        findNavController().navigate(action)
    }


    private fun handleEvent(event: FeedViewModel.Event) {
        when (event) {
            is FeedViewModel.Event.OnChangeFavoriteTopics -> {
                topicAdapter.submitList(event.topics)
            }
            is FeedViewModel.Event.OnSelectPictures -> {
                navigateFeedPost(event.uris)
            }
            is FeedViewModel.Event.NotExistTopicIdException -> {
                sendSnackBar("해당 토픽은 존재하지 않습니다용")
            }
            is FeedViewModel.Event.InvalidRequestException -> {
                sendSnackBar("정보를 제대로 입력하십시오")
            }
            is FeedViewModel.Event.InvalidTokenException -> {
                sendSnackBar("유효하지 않은 토큰입니다")
            }
            is FeedViewModel.Event.NotHavePermissionException -> {
                sendSnackBar("당신 권한이 없어요")
            }
            is FeedViewModel.Event.UnknownException -> {
                sendSnackBar("알 수 없는 에러 발생")
            }
            is FeedViewModel.Event.ExistedFollowingIdException -> {
                sendSnackBar("이미 팔로우 중입니다")
            }
            is FeedViewModel.Event.CreateAndDeleteFollowSuccess -> {
                sendSnackBar("정상적으로 실행")
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvCommunity.removeOnScrollListener(infinityScrollListener)
        binding.rvCommunity.adapter = null
        binding.rvTopic.adapter = null
        super.onDestroyView()
    }
}
