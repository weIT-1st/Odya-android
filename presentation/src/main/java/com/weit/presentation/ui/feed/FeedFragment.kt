package com.weit.presentation.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.domain.model.Community
import com.weit.domain.model.FeedItem
import com.weit.domain.model.MayKnowFriend
import com.weit.domain.model.PopularSpot
import com.weit.presentation.databinding.FragmentFeedBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(
    FragmentFeedBinding::inflate,
) {

    private val viewModel: FeedViewModel by viewModels()
    private val adapter = FeedAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        initRecyclerView()
    }
    private fun initRecyclerView() {
        val items = mutableListOf<FeedItem>()

        val communityList = listOf(Community("yes","no"),Community("yes","no"))
        val communityItem = communityList.map {
            FeedItem.CommunityItem(it.title,it.nickname)
        }
        for(c in communityItem){
            items.add(c)
        }
        adapter.items = items
        binding.rvCommunity.adapter = adapter

        val popularSpotList = listOf(PopularSpot("ddd","Ddd"),PopularSpot("ddd","Ddd"))
        val popularSpotItem = FeedItem.PopularSpotItem(popularSpotList)
        items.add(popularSpotItem)

        val communityList2 = listOf(Community("yes","no"),Community("yes","no"))
        val communityItem2 = communityList2.map {
            FeedItem.CommunityItem(it.title,it.nickname)
        }
        for(c in communityItem2){
            items.add(c)
        }
        val mayKnowFriendList = listOf(MayKnowFriend("swe","함께 아는 친구 2명"),MayKnowFriend("ddd","함께 아는 친구 1명"),
            MayKnowFriend("wef","yghggg"))
        val mayKnowFriendItem = FeedItem.MayknowFriendItem(mayKnowFriendList)
        items.add(mayKnowFriendItem)

        val communityList3 = listOf(Community("yes","no"),Community("yes","no"))
        val communityItem3 = communityList3.map {
            FeedItem.CommunityItem(it.title,it.nickname)
        }
        for(c in communityItem3){
            items.add(c)
        }
    }
    override fun initCollector() {
    }
}
