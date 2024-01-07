package com.weit.presentation.ui.profile.otherprofile

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFriendProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.profile.OtherProfileFragmentArgs
import com.weit.presentation.ui.profile.favoriteplace.FavoritePlaceAdapter
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class OtherProfileFragment() : BaseFragment<FragmentFriendProfileBinding>(
    FragmentFriendProfileBinding::inflate,
) {

    private val args: OtherProfileFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: OtherProfileViewModel.OtherProfileFactory

    private val viewModel: OtherProfileViewModel by viewModels {
        OtherProfileViewModel.provideFactory(viewModelFactory, args.userName)
    }

    private val otherProfileLifeShotAdapter = OtherProfileLifeShotAdapter()
    private val favoritePlaceAdapter = FavoritePlaceAdapter(
        selectPlace = { place ->
            viewModel.selectFavoritePlace(place)
        }
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO 팔로우
        //TODO 관심장소
        //TODO 유저검색해서 최종확인
        initRecyclerView()

    }

    private fun initRecyclerView() {
        binding.rvProfileLifeshot.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    rightDP = R.dimen.item_feed_comment_space,
                ),
            )
            addOnScrollListener(infinityScrollListener)
            adapter = otherProfileLifeShotAdapter
        }
        binding.rvProfileFavoritePlace.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    bottomDP = R.dimen.item_feed_comment_space,
                ),
            )
            adapter = favoritePlaceAdapter
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextLifeShots()
            }
        }
    }

    override fun initListener() {
//        binding.btnProfileFavoritePlaceMore.setOnClickListener {
//            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentMap()
//            findNavController().navigate(action)
//        }
        binding.btProfileFriendFollow.setOnClickListener {
            viewModel.onFollowStateChange()
        }
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.lifeshots.collectLatest { lifeshots ->
                if (!lifeshots.isNullOrEmpty()) {
                    Glide.with(binding.root)
                        .load(lifeshots.first().imageUrl)
                        .into(binding.ivProfileBg)
                    otherProfileLifeShotAdapter.submitList(lifeshots)
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaces.collectLatest { list ->
                favoritePlaceAdapter.submitList(list)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.followState.collectLatest { followingState ->
                val followImage = if(followingState) R.drawable.bt_following else R.drawable.bt_unfollow_fill
                binding.btProfileFriendFollow.setImageResource(followImage)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.userInfo.collectLatest { userInfo ->
                if (userInfo != null) {
                    Glide.with(binding.root)
                        .load(userInfo.user.profile.url)
                        .into(binding.ivProfileUser)
                    binding.tvProfileNickname.text = userInfo.user.nickname
                    binding.tvProfileTotalOdyaCount.text =
                        userInfo.userStatistics.odyaCount.toString()
                    binding.tvProfileTotalFollowingCount.text =
                        userInfo.userStatistics.followingsCount.toString()
                    binding.tvProfileTotalFollowCount.text =
                        userInfo.userStatistics.followersCount.toString()
                    val baseString =
                        getString(
                            R.string.profile_total_travel_count,
                            userInfo.user.nickname,
                            userInfo.userStatistics.travelPlaceCount,
                            userInfo.userStatistics.travelJournalCount
                        )

                    binding.tvProfileTotalTravelCount.text = HtmlCompat.fromHtml(
                        baseString,
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                    )

                }
            }
        }
    }

    private fun handleEvent(event: OtherProfileViewModel.Event) {

        when (event) {
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvProfileLifeshot.removeOnScrollListener(infinityScrollListener)
        binding.rvProfileLifeshot.adapter = null
        binding.rvProfileFavoritePlace.adapter = null
        super.onDestroyView()
    }
}
