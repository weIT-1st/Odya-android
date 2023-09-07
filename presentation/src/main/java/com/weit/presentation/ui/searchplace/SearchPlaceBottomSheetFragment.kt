package com.weit.presentation.ui.searchplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentBottomSheetPlaceSearchBinding
import com.weit.presentation.ui.placereview.EditPlaceReviewFragment
import com.weit.presentation.ui.searchplace.community.PlaceCommunityFragment
import com.weit.presentation.ui.searchplace.journey.PlaceJourneyFragment
import com.weit.presentation.ui.searchplace.review.PlaceReviewFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SearchPlaceBottomSheetFragment(
    private val placeId: String
): BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: SearchPlaceBottomSheetViewModel.PlaceIdFactory

    private val viewModel: SearchPlaceBottomSheetViewModel by viewModels{
        SearchPlaceBottomSheetViewModel.provideFactory(viewModelFactory, placeId)
    }

    private var _binding: FragmentBottomSheetPlaceSearchBinding? = null
    private val binding get() = _binding!!

//    private var searchPlaceBottomSheetAdapter: SearchPlaceBottomSheetAdapter? = null
    private val experiencedFriendAdapter: ExperiencedFriendAdapter by lazy{
        ExperiencedFriendAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetPlaceSearchBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabViewPager()
        initExperiencedFriendRV()

        binding.tvBsPlaceExperiencedFriend.text = String.format(
            getString(R.string.place_experienced_friend_count),
            viewModel.experiencedFriendNum
        )

        binding.btnBsPlaceBookMark.setOnClickListener {
            EditPlaceReviewFragment(placeId, null).show(childFragmentManager, "edit")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTabViewPager(){
        val viewPager = binding.viewPagerBsPlace
        val tabLayout = binding.tlBsPlace

        val tabItem = ArrayList<Fragment>()
        tabItem.add(PlaceJourneyFragment())
        tabItem.add(PlaceReviewFragment(placeId))
        tabItem.add(PlaceCommunityFragment())

        viewPager.apply {
            adapter = SearchPlaceBottomSheetAdapter(this.findFragment(), tabItem)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            when(position){
                0 -> tab.text = getString(R.string.place_journey)
                1 -> tab.text = getString(R.string.place_review)
                2 -> tab.text = getString(R.string.place_community)
            }
        }.attach()
    }

    private fun initExperiencedFriendRV(){
        binding.rvPlaceExperiencedFriendProfile.adapter = experiencedFriendAdapter
    }
}
