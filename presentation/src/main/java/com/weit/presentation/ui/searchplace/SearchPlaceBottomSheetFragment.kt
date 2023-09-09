package com.weit.presentation.ui.searchplace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
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

        // 임시 리뷰 추가 버튼 입니다 디자인 변경 나오면 삭제 후 수정하겠습니다.
        binding.btnBsPlaceBookMark.setOnClickListener {
            EditPlaceReviewFragment(placeId, null).show(childFragmentManager, "edit")
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.experiencedFriendNum.collectLatest {
                binding.tvBsPlaceExperiencedFriend.text = String.format(
                    getString(R.string.place_experienced_friend_count), it
                )
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.experiencedFriend.collectLatest {
                experiencedFriendAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest {event ->
                handelEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaceExperiencedFriendProfile.adapter = null
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

    private fun handelEvent(event: SearchPlaceBottomSheetViewModel.Event){
        when(event){
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
}
