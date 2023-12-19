package com.weit.presentation.ui.profile

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FeedFragmentDirections
import com.weit.presentation.ui.feed.detail.menu.FeedDetailMyMenuFragment
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.format.TextStyle
import java.util.Locale

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
            if (profileMenuDialog == null) {
                profileMenuDialog = ProfileMenuFragment { uri ->
                    Glide.with(binding.root)
                        .load(uri)
                        .into(binding.ivProfileUser)
                }

            }
            if (profileMenuDialog?.isAdded?.not() == true) {
                profileMenuDialog?.show(
                    requireActivity().supportFragmentManager,
                    ProfileMenuFragment.TAG,
                )
            }

        }

        binding.tvProfileMyCommunity.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentFeedMyActivity()
            findNavController().navigate(action)
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
                val baseString=
                    getString(
                        R.string.profile_total_travel_count,
                        event.user.nickname,
                        event.statistics.travelPlaceCount,
                        event.statistics.travelJournalCount
                    )

                val spannableString = SpannableString(baseString)

                val placesStart = baseString.indexOf("%1\$d")
                val placesEnd = placesStart + "%1\$d".length

                val logsStart = baseString.indexOf("%2\$d")
                val logsEnd = logsStart + "%2\$d".length

                spannableString.apply{
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(
                        requireContext(),
                        R.color.primary
                    )), placesStart, placesEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(
                        requireContext(),
                        R.color.primary
                    )), logsStart, logsEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                binding.tvProfileTotalTravelCount.text = spannableString
            }
            else -> {}
        }
    }

}
