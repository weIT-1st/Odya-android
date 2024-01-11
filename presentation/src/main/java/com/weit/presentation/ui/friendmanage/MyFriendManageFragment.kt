package com.weit.presentation.ui.friendmanage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.orhanobut.logger.Logger
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFriendManageBinding
import com.weit.presentation.model.Follow
import com.weit.presentation.model.Visibility
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.post.travelfriend.TravelFriendViewModel
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyFriendManageFragment : BaseFragment<FragmentFriendManageBinding>(
    FragmentFriendManageBinding::inflate,
) {
    private val viewModel: MyFriendManageViewModel by viewModels()

    private val followingSearchAdapter = FollowingSearchAdapter(
        onClickFollowing = { friend -> selectFollowing(friend)}
    )

    private val myFollowingAdapter = MyFollowingAdapter(
        onClickFollowing = { friend -> selectFollowing(friend)}
    )

    private val followerSearchAdapter = FollowerSearchAdapter(
        onClickFollower = { user -> selectFollower(user)}
    )

    private val myFollowerAdapter = MyFollowerAdapter(
        onClickFollower = { user -> selectFollower(user)}
    )

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.selectFollowMenu(Follow.fromPosition(tab.position))
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    private fun selectFollowing(friend : FollowUserContent){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.friend_manage_following_delete_title))
            .setMessage(getString(R.string.friend_manage_following_delete_content))
            .setNegativeButton(getString(R.string.friend_manage_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.friend_manage_delete)) { dialog, which ->
                viewModel.selectFriend(friend)
            }
            .show()
    }

    private fun selectFollower(friend : FollowUserContent){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.friend_manage_follower_delete_title))
            .setMessage(getString(R.string.friend_manage_follower_delete_content))
            .setNegativeButton(getString(R.string.friend_manage_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.friend_manage_delete)) { dialog, which ->
                viewModel.selectFriend(friend)
            }
            .show()
    }

    private val infinitySearchScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onSearchMoreUser()
            }
        }
    }

    private val infinityDefaultScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onDefault()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.tlFriendManageFollow.addOnTabSelectedListener(tabSelectedListener)
        initRv()
    }

    private fun initRv(){
        binding.rvFriendManageSearchFollower.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinitySearchScrollListener)
            adapter = followerSearchAdapter
        }
        binding.rvFriendManageSearchFollowing.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinitySearchScrollListener)
            adapter = followingSearchAdapter
        }
        binding.rvFriendManageDefaultFollower.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityDefaultScrollListener)
            adapter = myFollowerAdapter
        }
        binding.rvFriendManageDefaultFollowing.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityDefaultScrollListener)
            adapter = myFollowingAdapter
        }
    }

    override fun initListener() {
        binding.tbFriendManage.setNavigationOnClickListener {
            findNavController().popBackStack()
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
                val isQueryEmpty = query.isEmpty()
                if (!isQueryEmpty) {
                    viewModel.updateSearchRv()
                    viewModel.onSearch(query)
                }else{
                    viewModel.updateDefaultRv()
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResultFollowings.collectLatest { users ->
                followingSearchAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchResultFollowers.collectLatest { users ->
                followerSearchAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.defaultFollowers.collectLatest { users ->
                myFollowerAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.defaultFollowings.collectLatest { users ->
                myFollowingAdapter.submitList(users)
            }
        }
    }

    private fun handleEvent(event: MyFriendManageViewModel.Event) {
        when (event) {
            is MyFriendManageViewModel.Event.OnUpdateSearchRv -> {
                if(event.followState == Follow.FOLLOWING) {
                    binding.rvFriendManageSearchFollowing.isVisible = true
                    binding.rvFriendManageSearchFollower.isVisible = false
                    binding.rvFriendManageDefaultFollowing.isVisible = false
                    binding.rvFriendManageDefaultFollower.isVisible = false
                }else{
                    binding.rvFriendManageSearchFollowing.isVisible = false
                    binding.rvFriendManageSearchFollower.isVisible = true
                    binding.rvFriendManageDefaultFollowing.isVisible = false
                    binding.rvFriendManageDefaultFollower.isVisible = false
                }
            }
            is MyFriendManageViewModel.Event.OnUpdateDefaultRv -> {
                if(event.followState == Follow.FOLLOWING) {
                    binding.rvFriendManageSearchFollowing.isVisible = false
                    binding.rvFriendManageSearchFollower.isVisible = false
                    binding.rvFriendManageDefaultFollowing.isVisible = true
                    binding.rvFriendManageDefaultFollower.isVisible = false
                }else{
                    binding.rvFriendManageSearchFollowing.isVisible = false
                    binding.rvFriendManageSearchFollower.isVisible = false
                    binding.rvFriendManageDefaultFollowing.isVisible = false
                    binding.rvFriendManageDefaultFollower.isVisible = true
                }
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvFriendManageSearchFollowing.removeOnScrollListener(infinitySearchScrollListener)
        binding.rvFriendManageSearchFollower.removeOnScrollListener(infinitySearchScrollListener)
        binding.rvFriendManageDefaultFollower.removeOnScrollListener(infinityDefaultScrollListener)
        binding.rvFriendManageDefaultFollowing.removeOnScrollListener(infinityDefaultScrollListener)
        super.onDestroyView()
    }
}
