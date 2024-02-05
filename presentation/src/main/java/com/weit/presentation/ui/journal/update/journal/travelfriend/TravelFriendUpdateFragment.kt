package com.weit.presentation.ui.journal.update.journal.travelfriend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelFriendUpdateBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.post.travelfriend.TravelFriendFragmentArgs
import com.weit.presentation.ui.post.travelfriend.TravelFriendProfileAdapter
import com.weit.presentation.ui.post.travelfriend.TravelFriendSearchAdapter
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TravelFriendUpdateFragment : BaseFragment<FragmentTravelFriendUpdateBinding>(
    FragmentTravelFriendUpdateBinding::inflate
) {
    private val args: TravelFriendUpdateFragmentArgs by navArgs()
    private val viewModel : TravelFriendUpdateViewModel by viewModels()

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
        initRecyclerView()
        // todo
        viewModel.initTravelFriends(args.followers?.toList())
    }

    override fun initListener() {
        binding.tbTravelFriend.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_complete) {
                viewModel.onComplete()
            }
            true
        }
        binding.tbTravelFriend.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest{ event ->
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

    override fun onDestroyView() {
        binding.rvTravelFriendSearch.adapter = null
        binding.rvTravelFriendSearch.removeOnScrollListener(infinityScrollListener)
        binding.rvTravelFriendProfile.adapter = null
        super.onDestroyView()
    }

    private fun initRecyclerView() {
        binding.rvTravelFriendProfile.run {
            addItemDecoration(SpaceDecoration(resources, leftDP = R.dimen.item_travel_friend_profile_space))
            adapter = travelFriendProfileAdapter
        }

        binding.rvTravelFriendSearch.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityScrollListener)
            adapter = travelFriendSearchAdapter
        }
    }

    private fun handleEvent(event: TravelFriendUpdateViewModel.Event){
        when (event) {
            is TravelFriendUpdateViewModel.Event.OnChangeTravelFriends -> {
                travelFriendProfileAdapter.submitList(event.travelFriends)
            }
            is TravelFriendUpdateViewModel.Event.OnComplete -> {
                val action = TravelFriendUpdateFragmentDirections.actionTravelFriendUpdateFragmentToTravelJournalUpdateFragment(
                    event.travelFriends.toTypedArray(),
                    args.travelJounalUpdateDTO
                )
                findNavController().navigate(action)
            }
        }
    }
}
