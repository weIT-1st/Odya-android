package com.weit.presentation.ui.searchplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetPlaceSearchBinding
import com.weit.presentation.ui.searchplace.community.PlaceCommunityFragment
import com.weit.presentation.ui.searchplace.journey.PlaceJourneyFragment
import com.weit.presentation.ui.searchplace.review.PlaceReviewFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SearchPlaceBottomSheetFragment(
    private val placeId: String,
    private val reset : () -> Unit
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: SearchPlaceBottomSheetViewModel.PlaceIdFactory

    private val viewModel: SearchPlaceBottomSheetViewModel by viewModels {
        SearchPlaceBottomSheetViewModel.provideFactory(viewModelFactory, placeId)
    }

    private var _binding: BottomSheetPlaceSearchBinding? = null
    private val binding get() = _binding!!

    private val experiencedFriendAdapter: ExperiencedFriendAdapter by lazy {
        ExperiencedFriendAdapter()
    }

    private val placeHolder = R.layout.image_placeholder.toDrawable()

    private val tabItem = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetPlaceSearchBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initExperiencedFriendRV()
        initTabViewPager()

        tabItem.add(tabJourney, PlaceJourneyFragment())
        tabItem.add(tabReview, PlaceReviewFragment(placeId, ""))
        tabItem.add(tabCommunity, PlaceCommunityFragment())

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.experiencedFriendNum.collectLatest {
                binding.tvBsPlaceExperiencedFriend.text = String.format(
                    getString(R.string.place_experienced_friend_count),
                    it,
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.experiencedFriend.collectLatest {
                experiencedFriendAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.placeImage.collectLatest { bitmap ->
                Glide.with(requireContext())
                    .load(bitmap)
                    .placeholder(placeHolder)
                    .into(binding.ivBsPlaceThumbnail)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.placeTitle.collectLatest { title ->
                binding.tvBsPlaceTitle.text = title
                changeTitle(title)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.placeAddress.collectLatest { address ->
                binding.tvBsPlaceAddress.text = address
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaceExperiencedFriendProfile.adapter = null
        binding.viewPagerBsPlace.adapter = null
        _binding = null
        reset()
    }

    private fun initTabViewPager() {
        val viewPager = binding.viewPagerBsPlace
        val tabLayout = binding.tlBsPlace

        tabItem.add(tabJourney, PlaceJourneyFragment())
        tabItem.add(tabReview, PlaceReviewFragment(placeId, ""))
        tabItem.add(tabCommunity, PlaceCommunityFragment())

        viewPager.apply {
            adapter = SearchPlaceBottomSheetAdapter(this.findFragment(), tabItem)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                tabJourney -> tab.text = getString(R.string.place_journey)
                tabReview -> tab.text = getString(R.string.place_review)
                tabCommunity -> tab.text = getString(R.string.place_community)
            }
        }.attach()
    }

    private fun changeTitle(placeTitle: String){
        val viewPager = binding.viewPagerBsPlace

        viewPager.apply {
            adapter = SearchPlaceBottomSheetAdapter(this.findFragment(), tabItem)
            isUserInputEnabled = false
        }

    }


    private fun initExperiencedFriendRV() {
        binding.rvPlaceExperiencedFriendProfile.adapter = experiencedFriendAdapter
    }

    private fun handelEvent(event: SearchPlaceBottomSheetViewModel.Event) {
        when (event) {
            is SearchPlaceBottomSheetViewModel.Event.GetExperiencedFriendSuccess -> {
                sendSnackBar("방문한 친구 조회 성공!")
            }
            is SearchPlaceBottomSheetViewModel.Event.InvalidRequestException -> {
                sendSnackBar("잘못된 placeId를 가져오고 있어요")
            }
            is SearchPlaceBottomSheetViewModel.Event.InvalidTokenException -> {
                sendSnackBar("유효하지 않은 토큰 입니다.")
            }
            is SearchPlaceBottomSheetViewModel.Event.UnknownException -> {
                sendSnackBar("알 수 없는 에러 발생")
            }
        }
    }

    private fun sendSnackBar(
        message: String,
        @IntRange(from = -2) length: Int = Snackbar.LENGTH_SHORT,
        anchorView: View? = null,
    ) {
        Snackbar.make(
            binding.root,
            message,
            length,
        ).apply {
            if (anchorView != null) {
                this.anchorView = anchorView
            }
        }.show()
    }

    companion object {
        private const val tabJourney = 0
        private const val tabReview = 1
        private const val tabCommunity = 2
    }
}