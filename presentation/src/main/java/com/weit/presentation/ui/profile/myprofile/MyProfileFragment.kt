package com.weit.presentation.ui.profile.myprofile

import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.model.profile.lifeshot.LifeShotRequestDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.FavoriteTopicAdapter
import com.weit.presentation.ui.profile.favoriteplace.FavoritePlaceAdapter
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.profile.reptraveljournal.RepTravelJournalAdapter
import com.weit.presentation.ui.util.DimensionUtils
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

    private val repTravelJournalAdapter = RepTravelJournalAdapter(
        selectTravelJournal = { journal ->
            //TODO 여행일지 페이지로 이동
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        viewModel.initData()
        initRecyclerView()

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

        binding.tvProfileMyCommunity.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentFeedMyActivity()
            findNavController().navigate(action)
        }

        binding.tvProfileRepTravelJournal.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToRepTravelJournalFragment()
            findNavController().navigate(action)
        }

        binding.btnProfileFavoritePlaceMore.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentMap()
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

    private val repInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextRepTravelJournals()
            }
        }
    }

    private fun initRecyclerView(){
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
        binding.rvProfileTravelJournal.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.main_my_journal_margin))
            addOnScrollListener(repInfinityScrollListener)
            adapter = repTravelJournalAdapter
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
                if (!lifeshots.isNullOrEmpty()) {
                    Glide.with(binding.root)
                        .load(lifeshots.first().imageUrl)
                        .into(binding.ivProfileBg)
                    myProfileLifeShotAdapter.submitList(lifeshots)
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaceCount.collectLatest { count ->
                binding.btnProfileFavoritePlaceMore.text = getString(
                    R.string.profile_bookmark_place,
                    count
                )
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaces.collectLatest { list ->
                favoritePlaceAdapter.submitList(list)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.repTravelJournals.collectLatest { list ->
                repTravelJournalAdapter.submitList(list)
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
        binding.rvProfileTravelJournal.removeOnScrollListener(repInfinityScrollListener)
        binding.rvProfileLifeshot.adapter = null
        binding.rvProfileFavoritePlace.adapter = null
        binding.rvProfileTravelJournal.adapter = null
        super.onDestroyView()
    }
}
