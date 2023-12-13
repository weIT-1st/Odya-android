package com.weit.presentation.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.menu.FeedDetailMyMenuFragment
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(
    FragmentMyProfileBinding::inflate,
) {

    private val viewModel: MyProfileViewModel by viewModels()
    private var profileMenuDialog: ProfileMenuFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel




    }

    override fun initListener() {
        binding.ivProfileImage.setOnClickListener {
//            if (profileMenuDialog == null) {
//                profileMenuDialog = ProfileMenuFragment {
//                        uri -> bi
//                }
//
//            }
//            if (myMenuDialog?.isAdded?.not() == true) {
//            myMenuDialog?.show(
//                requireActivity().supportFragmentManager,
//                FeedDetailMyMenuFragment.TAG,
//            )
        }

    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: MyProfileViewModel.Event) {
        when (event) {
            is MyProfileViewModel.Event.GetUserStatisticsSuccess -> {
                binding.tvProfileTotalOdyaCount.text = event.statistics.odyaCount.toString()
                binding.tvProfileTotalFollowingCount.text = event.statistics.followingsCount.toString()
                binding.tvProfileTotalFollowCount.text = event.statistics.followersCount.toString()
                binding.tvProfileTotalTravelCount.text = event.statistics.travelPlaceCount.toString()
                binding.tvProfileTravelJournalContent.text = event.statistics.travelJournalCount.toString()
            }
            else -> {}
        }
    }

}
