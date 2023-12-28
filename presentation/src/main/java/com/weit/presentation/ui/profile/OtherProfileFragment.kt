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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFriendProfileBinding
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FeedFragmentDirections
import com.weit.presentation.ui.feed.detail.FeedDetailFragmentArgs
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.feed.detail.menu.FeedDetailMyMenuFragment
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class OtherProfileFragment() : BaseFragment<FragmentFriendProfileBinding>(
    FragmentFriendProfileBinding::inflate,
) {

//    private val args: Other by navArgs()

    private val viewModel: OtherProfileViewModel by viewModels()

    @Inject
    lateinit var viewModelFactory: FeedDetailViewModel.FeedDetailFactory
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO 유저인생샷
        //TODO 즐겨찾기 여행일지
        //TODO 대표여행일지
        //TODO 관심장소

    }

    override fun initListener() {
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: OtherProfileViewModel.Event) {

        when (event) {
            is OtherProfileViewModel.Event.GetUserStatisticsSuccess -> {
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
