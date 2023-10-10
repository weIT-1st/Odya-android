package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.orhanobut.logger.Logger
import com.weit.presentation.databinding.FragmentFeedBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(
    FragmentFeedBinding::inflate,
) {

    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter = FeedAdapter(
        navigateTravelLog = { travelLogId -> navigateTravelLog(travelLogId) },
        navigateFeedDetail = { feedId -> navigateFeedDetail(feedId) },
        onFollowChanged = { userId, isChecked -> viewModel.onFollowStateChange(userId, isChecked) },
        scrollListener = { viewModel.onNextFriends() }
        )
    private val topicAdapter = FavoriteTopicAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initTopicRecyclerView()
        initCommunityRecyclerView()
        binding.btnWrite.setOnClickListener {
            navigateFeedPost()
        }
    }

    private fun initTopicRecyclerView() {
        binding.rvTopic.adapter = topicAdapter
    }
    private fun initCommunityRecyclerView() {
        binding.rvCommunity.adapter = feedAdapter
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


    private fun navigateTravelLog(travelLogId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentTravellog(travelLogId)
        findNavController().navigate(action)
    }

    private fun navigateFeedDetail(feedId: Long) {
        val action = FeedFragmentDirections.actionFragmentFeedToFragmentFeedDetail(feedId)
        findNavController().navigate(action)
    }

    private fun navigateFeedPost() {
        val action = FeedFragmentDirections.actionFragmentFeedToFeedPostFragment()
        findNavController().navigate(action)
    }
    private fun handleEvent(event: FeedViewModel.Event) {
        when (event) {
            is FeedViewModel.Event.OnChangeFavoriteTopics -> {
                topicAdapter.submitList(event.topics)
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
}
