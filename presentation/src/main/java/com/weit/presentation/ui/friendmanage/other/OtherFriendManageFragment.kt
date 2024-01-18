package com.weit.presentation.ui.friendmanage.other

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.weit.domain.model.follow.FollowUserContent
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentOtherFriendManageBinding
import com.weit.presentation.model.Follow
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.friendmanage.showDeleteFollowingDialog
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class OtherFriendManageFragment : BaseFragment<FragmentOtherFriendManageBinding>(
    FragmentOtherFriendManageBinding::inflate,
) {
    private val args: OtherFriendManageFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: OtherFriendManageViewModel.OtherFriendManageFactory


    private val viewModel: OtherFriendManageViewModel by viewModels {
        OtherFriendManageViewModel.provideFactory(viewModelFactory, args.userId)
    }

    private val followingSearchAdapter = OtherFollowingSearchAdapter { user ->
        if (user.isFollowing) {
            context?.let { showDeleteFollowingDialog(it) { viewModel.deleteFollow(user, SEARCH_FOLLOWING) } }
        } else viewModel.createFollow(user, SEARCH_FOLLOWING)
    }


    private val myFollowingAdapter = OtherFollowingAdapter { user ->
        if (user.isFollowing) {
            context?.let { showDeleteFollowingDialog(it) { viewModel.deleteFollow(user, DEFAULT_FOLLOWING) } }
        } else viewModel.createFollow(user, DEFAULT_FOLLOWING)
    }


    private val otherFollowerSearchAdapter = OtherFollowerSearchAdapter { user ->
            if(user.isFollowing) {
                context?.let { showDeleteFollowingDialog(it) { viewModel.deleteFollow(user, SEARCH_FOLLOWER) } }
            } else viewModel.createFollow(user,SEARCH_FOLLOWER)
        }


    private val otherFollowerAdapter = OtherFollowerAdapter{ user ->
        if(user.isFollowing) {
            context?.let { showDeleteFollowingDialog(it) { viewModel.deleteFollow(user, DEFAULT_FOLLOWER) } }
        } else viewModel.createFollow(user,DEFAULT_FOLLOWER)
    }


    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.selectFollowMenu(Follow.fromPosition(tab.position))
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
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
            adapter = otherFollowerSearchAdapter
        }
        binding.rvFriendManageSearchFollowing.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinitySearchScrollListener)
            adapter = followingSearchAdapter
        }
        binding.rvFriendManageDefaultFollower.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_travel_friend_search_space))
            addOnScrollListener(infinityDefaultScrollListener)
            adapter = otherFollowerAdapter
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
                    viewModel.initData()
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
                otherFollowerSearchAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.defaultFollowers.collectLatest { users ->
                otherFollowerAdapter.submitList(users)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.defaultFollowings.collectLatest { users ->
                myFollowingAdapter.submitList(users)
            }
        }
    }

    private fun handleEvent(event: OtherFriendManageViewModel.Event) {
        when (event) {
            is OtherFriendManageViewModel.Event.OnUpdateSearchRv -> {
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
            is OtherFriendManageViewModel.Event.OnUpdateDefaultRv -> {
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
        binding.rvFriendManageDefaultFollowing.adapter = null
        binding.rvFriendManageDefaultFollower.adapter = null
        binding.rvFriendManageSearchFollower.adapter = null
        binding.rvFriendManageSearchFollowing.adapter = null
        super.onDestroyView()
    }

    companion object{
        const val SEARCH_FOLLOWER = "SEARCH_FOLLOWER"
        const val SEARCH_FOLLOWING = "SEARCH_FOLLOWING"
        const val DEFAULT_FOLLOWER = "DEFAULT_FOLLOWER"
        const val DEFAULT_FOLLOWING = "DEFAULT_FOLLOWING"
    }
}
