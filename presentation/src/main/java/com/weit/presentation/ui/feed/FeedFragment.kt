package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.model.Community
import com.weit.presentation.model.FeedItem
import com.weit.presentation.model.MayKnowFriend
import com.weit.presentation.model.PopularSpot
import com.weit.presentation.databinding.FragmentFeedBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(
    FragmentFeedBinding::inflate,
) {

    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter = FeedAdapter()
    private val topicAdapter = FavoriteTopicAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initTopicRecyclerView()
        initCommunityRecyclerView()
    }

    private fun initTopicRecyclerView(){
        binding.rvTopic.adapter = topicAdapter
    }
    private fun initCommunityRecyclerView() {
        val items = mutableListOf<FeedItem>()

        val communityList = listOf(Community("yes","no"), Community("yes","no"))
        val communityItem = communityList.map {
            FeedItem.CommunityItem(it.title,it.nickname)
        }
        for(c in communityItem){
            items.add(c)
        }
        feedAdapter.items = items
        binding.rvCommunity.adapter = feedAdapter

        val popularSpotList = listOf(PopularSpot("ddd","Ddd"), PopularSpot("ddd","Ddd"))
        val popularSpotItem = FeedItem.PopularSpotItem(popularSpotList)
        items.add(popularSpotItem)

        val communityList2 = listOf(Community("yes","no"), Community("yes","no"))
        val communityItem2 = communityList2.map {
            FeedItem.CommunityItem(it.title,it.nickname)
        }
        for(c in communityItem2){
            items.add(c)
        }
        val mayKnowFriendList = listOf(
            MayKnowFriend("swe","함께 아는 친구 2명"), MayKnowFriend("ddd","함께 아는 친구 1명"),
            MayKnowFriend("wef","yghggg")
        )
        val mayKnowFriendItem = FeedItem.MayknowFriendItem(mayKnowFriendList)
        items.add(mayKnowFriendItem)

        val communityList3 = listOf(Community("yes","no"), Community("yes","no"))
        val communityItem3 = communityList3.map {
            FeedItem.CommunityItem(it.title,it.nickname)
        }
        for(c in communityItem3){
            items.add(c)
        }
    }
    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoriteTopics.collectLatest { topics ->
                topicAdapter.submitList(topics)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: FeedViewModel.Event) {
        when (event) {
            FeedViewModel.Event.NotExistTopicIdException -> {
                sendSnackBar("해당 토픽은 존재하지 않습니다용")
            }
            FeedViewModel.Event.InvalidRequestException -> {
                sendSnackBar("정보를 제대로 입력하십시오")
            }
            FeedViewModel.Event.InvalidTokenException -> {
                sendSnackBar("유효하지 않은 토큰입니다")
            }
            FeedViewModel.Event.NotHavePermissionException -> {
                sendSnackBar("당신 권한이 없어요")
            }
            else -> {}
        }
    }
}
