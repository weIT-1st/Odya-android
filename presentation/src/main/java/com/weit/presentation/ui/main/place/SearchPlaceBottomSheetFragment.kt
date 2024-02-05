package com.weit.presentation.ui.main.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetPlaceSearchBinding
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SearchPlaceBottomSheetFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: SearchPlaceBottomSheetViewModel.PlaceIdFactory

    private val args: SearchPlaceBottomSheetFragmentArgs by navArgs()
    private val viewModel: SearchPlaceBottomSheetViewModel by viewModels {
        SearchPlaceBottomSheetViewModel.provideFactory(viewModelFactory, args.placeId)
    }

    private var _binding: BottomSheetPlaceSearchBinding? = null
    private val binding get() = _binding!!

    private val experiencedFriendAdapter = ExperiencedFriendAdapter()
    private val placeHolder = R.layout.image_placeholder.toDrawable()

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

        binding.btnBsPlaceBookMark.setOnClickListener {
            viewModel.updateFavoritePlace()
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.experiencedFriendInfo.collectLatest { info ->
                binding.tvBsPlaceExperiencedFriend.text = String.format(getString(R.string.place_experienced_friend_count), info.count)
                experiencedFriendAdapter.submitList(info.summaryFriends)
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
            viewModel.placeInfo.collectLatest { info ->
                binding.tvBsPlaceTitle.text = info.title
                binding.tvBsPlaceAddress.text = info.address
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.isFavoritePlace.collectLatest { isFavorite ->
                if (isFavorite) {
                    binding.btnBsPlaceBookMark.setBackgroundResource(R.drawable.ic_book_mark_yellow)
                } else {
                    binding.btnBsPlaceBookMark.setBackgroundResource(R.drawable.ic_book_mark)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaceExperiencedFriendProfile.adapter = null
        binding.viewPagerBsPlace.adapter = null
        _binding = null
    }

    private fun initTabViewPager() {
        val viewPager = binding.viewPagerBsPlace
        val tabLayout = binding.tlBsPlace

        viewPager.apply {
            adapter = SearchPlaceTapAdapter(childFragmentManager, lifecycle, args.placeId)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                tabJournal -> tab.text = getString(R.string.place_journey)
                tabReview -> tab.text = getString(R.string.place_review)
                tabCommunity -> tab.text = getString(R.string.place_community)
            }
        }.attach()
    }

    private fun initExperiencedFriendRV() {
        binding.rvPlaceExperiencedFriendProfile.apply {
            adapter = experiencedFriendAdapter
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_travel_friends_space))
        }
    }

    companion object {
        private const val tabJournal = 0
        private const val tabReview = 1
        private const val tabCommunity = 2
    }
}
