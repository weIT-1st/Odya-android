package com.weit.presentation.ui.post.travelfriend

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelFriendBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TravelFriendFragment : BaseFragment<FragmentTravelFriendBinding>(
    FragmentTravelFriendBinding::inflate,
) {

    private val args: TravelFriendFragmentArgs by navArgs()
    private val viewModel: TravelFriendViewModel by navGraphViewModels(R.id.post_graph) {
        defaultViewModelProviderFactory
    }
    private val travelFriendSearchAdapter = TravelFriendSearchAdapter { follower ->
        viewModel.onAddFriend(follower)
    }
    private val travelFriendProfileAdapter = TravelFriendProfileAdapter { position ->
        viewModel.onRemoveFriend(position)
    }
    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextFollowers()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvTravelFriendProfile.run {
            addItemDecoration(SpaceDecoration(resources, leftDP = R.dimen.item_travel_friend_profile_space))
            adapter = travelFriendProfileAdapter
        }
        binding.rvTravelFriendSearch.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityScrollListener)
            adapter = travelFriendSearchAdapter
        }
        binding.rvTravelFriendSearch.adapter = travelFriendSearchAdapter
        viewModel.initTravelFriends(args.followers?.toList() ?: emptyList())
    }

    override fun initListener() {
        binding.tbTravelFriend.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_complete) {
                viewModel.onComplete()
            }
            true
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.query.collectLatest { query ->
                viewModel.onSearch(query)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.followers.collectLatest { followers ->
                travelFriendSearchAdapter.submitList(followers)
            }
        }
    }

    private fun handleEvent(event: TravelFriendViewModel.Event) {
        when (event) {
            is TravelFriendViewModel.Event.OnChangeTravelFriends -> {
                travelFriendProfileAdapter.submitList(event.travelFriends)
            }
            is TravelFriendViewModel.Event.OnComplete -> {
                val action = TravelFriendFragmentDirections.actionTravelFriendFragmentToPostTravelLogFragment(
                    event.travelFriends.toTypedArray(),
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvTravelFriendSearch.removeOnScrollListener(infinityScrollListener)
        super.onDestroyView()
    }
}
