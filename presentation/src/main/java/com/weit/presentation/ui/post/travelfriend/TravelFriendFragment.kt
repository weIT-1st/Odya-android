package com.weit.presentation.ui.post.travelfriend

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelFriendBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TravelFriendFragment : BaseFragment<FragmentTravelFriendBinding>(
    FragmentTravelFriendBinding::inflate,
) {
    private val args: TravelFriendFragmentArgs by navArgs()

    private val viewModel: TravelFriendViewModel by navGraphViewModels(R.id.post_graph) {
        defaultViewModelProviderFactory
    }

    private val travelFriendSearchAdapter = TravelFriendSearchAdapter { position ->
        viewModel.onAddFriend(position)
    }
    private val travelFriendProfileAdapter = TravelFriendProfileAdapter { position ->
        viewModel.onRemoveFriend(position)
    }
    private lateinit var textWatcher: TextWatcher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTravelFriendProfile.run {
            addItemDecoration(SpaceDecoration(resources, leftDP = R.dimen.item_travel_friend_profile_space))
            adapter = travelFriendProfileAdapter
        }
        binding.rvTravelFriendSearch.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            adapter = travelFriendSearchAdapter
        }
        binding.rvTravelFriendSearch.adapter = travelFriendSearchAdapter
        viewModel.initTravelFriends(args.followers?.toList() ?: emptyList())
    }

    override fun initListener() {
        textWatcher = binding.etTravelFriendSearch.addTextChangedListener {
            viewModel.onSearch(it.toString())
        }
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
    }

    private fun handleEvent(event: TravelFriendViewModel.Event) {
        when (event) {
            is TravelFriendViewModel.Event.OnChangeTravelFriends -> {
                travelFriendProfileAdapter.submitList(event.travelFriends)
                travelFriendSearchAdapter.submitList(event.followers)
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
        if (::textWatcher.isInitialized) {
            binding.etTravelFriendSearch.removeTextChangedListener(textWatcher)
        }
        super.onDestroyView()
    }
}
