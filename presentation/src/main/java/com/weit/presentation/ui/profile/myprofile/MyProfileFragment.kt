package com.weit.presentation.ui.profile.myprofile

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.model.profile.lifeshot.LifeShotRequestDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.profile.favoriteplace.FavoritePlaceAdapter
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(
    FragmentMyProfileBinding::inflate,
) {

    private val viewModel: MyProfileViewModel by viewModels()
    private var profileMenuDialog: ProfileMenuFragment? = null
    private val myProfileLifeShotAdapter = MyProfileLifeShotAdapter(
        selectImage = { lifeShotEntity, position ->
            viewModel.selectLifeShot(lifeShotEntity, position)
        }
    )
    private val favoritePlaceAdapter = FavoritePlaceAdapter(
        selectPlace = { place ->
            viewModel.deleteFavoritePlace(place)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        viewModel.initData()
        initRecyclerView()
        //TODO 즐겨찾기 여행일지
        //TODO 대표여행일지

    }

    override fun initListener() {
        binding.ivProfileUser.setOnClickListener {
            if (profileMenuDialog == null) {
                profileMenuDialog = ProfileMenuFragment { uri ->
                    if (uri == null) viewModel.getUserProfileNone() else Glide.with(binding.root)
                        .load(uri).into(binding.ivProfileUser)
                }
            }
            if (profileMenuDialog?.isAdded?.not() == true) {
                profileMenuDialog?.show(
                    requireActivity().supportFragmentManager,
                    ProfileMenuFragment.TAG,
                )
            }

        }

        binding.tvProfileLifeshotTitle.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentLifeShotPicker()
            findNavController().navigate(action)
        }

        binding.layoutProfileNoLifeshot.btnAddLifeshot.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentLifeShotPicker()
            findNavController().navigate(action)
        }

        binding.layoutProfileNoTravellog.btnFeedNoTravelLogWrite.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToPostGraph()
            findNavController().navigate(action)
        }

        binding.tvProfileMyCommunity.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentFeedMyActivity()
            findNavController().navigate(action)
        }

        binding.btnProfileFavoritePlaceMore.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentMap()
            findNavController().navigate(action)
        }
        binding.viewProfileTotalCount.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToMyFriendManageFragment()
            findNavController().navigate(action)
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextLifeShots()
            }
        }
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
            adapter = myProfileLifeShotAdapter
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

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.userProfile.collectLatest { uri ->
                Glide.with(binding.root)
                    .load(uri).into(binding.ivProfileUser)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.lifeshots.collectLatest { lifeshots ->
                if (lifeshots.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(lifeshots.first().imageUrl)
                        .into(binding.ivProfileBg)
                    binding.layoutProfileNoLifeshot.root.visibility = View.GONE
                    binding.rvProfileLifeshot.visibility = View.VISIBLE
                }else{
                    binding.layoutProfileNoLifeshot.root.visibility = View.VISIBLE
                    binding.rvProfileLifeshot.visibility = View.INVISIBLE
                }
                myProfileLifeShotAdapter.submitList(lifeshots)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaceCount.collectLatest { count ->
               if(count > 4){
                   binding.btnProfileFavoritePlaceMore.text = getString(
                       R.string.profile_bookmark_place,
                       count - 4
                   )
                   binding.btnProfileFavoritePlaceMore.visibility = View.VISIBLE
               }else{
                   binding.btnProfileFavoritePlaceMore.visibility = View.INVISIBLE
               }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaces.collectLatest { list ->
                if(list.isEmpty()){
                    binding.tvProfileNoFavoritePlace.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    binding.rvProfileFavoritePlace.visibility = View.INVISIBLE
                }else{
                    binding.tvProfileNoFavoritePlace.visibility = View.GONE
                    binding.view2.visibility = View.INVISIBLE
                    binding.rvProfileFavoritePlace.visibility = View.VISIBLE
                }
                favoritePlaceAdapter.submitList(list)
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
                    if(userInfo.userStatistics.travelJournalCount <= 0){
                            binding.layoutProfileNoTravellog.root.visibility = View.VISIBLE
                            binding.ivProfileImage.visibility = View.INVISIBLE
                            binding.tvProfileTotalTravelCount.visibility = View.INVISIBLE
                    }else{
                            binding.layoutProfileNoTravellog.root.visibility = View.GONE
                            binding.ivProfileImage.visibility = View.VISIBLE
                            binding.tvProfileTotalTravelCount.visibility = View.VISIBLE
                    }
                    binding.tvProfileTotalTravelCount.text = HtmlCompat.fromHtml(
                        baseString,
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                    )
                }
            }
        }
    }

    private fun handleEvent(event: MyProfileViewModel.Event) {

        when (event) {
            is MyProfileViewModel.Event.OnSelectLifeShot -> {
                val action =
                    MyProfileFragmentDirections.actionFragmentMypageToLifeShotDetailFragment(
                        event.lifeshots.toTypedArray(),
                        event.position,
                        LifeShotRequestDTO(event.lastImageId ?: 0, event.userId)
                    )
                findNavController().navigate(action)
            }

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
