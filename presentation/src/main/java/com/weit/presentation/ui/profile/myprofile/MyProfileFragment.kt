package com.weit.presentation.ui.profile.myprofile

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMyProfileBinding
import com.weit.presentation.model.profile.lifeshot.LifeShotRequestDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.journal.map.PinMode
import com.weit.presentation.ui.journal.map.TravelJournalMapFragment
import com.weit.presentation.ui.profile.bookmarkjournal.ProfileBookmarkJournalAdapter
import com.weit.presentation.ui.profile.favoriteplace.FavoritePlaceAdapter
import com.weit.presentation.ui.profile.menu.ProfileMenuFragment
import com.weit.presentation.ui.profile.reptraveljournal.RepTravelJournalFriendAdapter
import com.weit.presentation.ui.profile.reptraveljournal.TogetherFriendBottomFragment
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

    private val bookmarkJournalAdapter = ProfileBookmarkJournalAdapter(
        showDetail = { moveToJournalDetail(it) },
        updateBookmarkState = { viewModel.updateBookmarkTravelJournalBookmarkState(it) }
    )

    private val repJournalFriendAdapter = RepTravelJournalFriendAdapter()
    private var togetherFriendBottomFragment: TogetherFriendBottomFragment? = null

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
            viewModel.updateRepTravelJournal()
        }

        binding.btnProfileFavoritePlaceMore.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentMap()
            findNavController().navigate(action)
        }
        binding.viewProfileTotalCount.setOnClickListener {
            val action = MyProfileFragmentDirections.actionFragmentMypageToMyFriendManageFragment()
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
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextLifeShots()
            }
        }
    }

    private val bookMarkJournalInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextBookMarkJournal()
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

        binding.rvProfileBookmarkTravelJournal.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    rightDP = R.dimen.item_feed_comment_space,
                ),
            )
            adapter = bookmarkJournalAdapter
        }

        binding.itemProfileRepTravelJournal.rvItemMyJournalFriends.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    rightDP = R.dimen.item_journal_friends_space,
                ),
            )
            adapter = repJournalFriendAdapter
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
                } else {
                    binding.layoutProfileNoLifeshot.root.visibility = View.VISIBLE
                    binding.rvProfileLifeshot.visibility = View.INVISIBLE
                }
                myProfileLifeShotAdapter.submitList(lifeshots)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaceCount.collectLatest { count ->
                if (count > DEFAULT_FAVORITE_PLACE_COUNT) {
                    binding.btnProfileFavoritePlaceMore.text = getString(
                        R.string.profile_bookmark_place,
                        count - 4
                    )
                    binding.btnProfileFavoritePlaceMore.visibility = View.VISIBLE
                } else {
                    binding.btnProfileFavoritePlaceMore.visibility = View.INVISIBLE
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaces.collectLatest { list ->
                if (list.isEmpty()) {
                    binding.tvProfileNoFavoritePlace.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    binding.rvProfileFavoritePlace.visibility = View.INVISIBLE
                } else {
                    binding.tvProfileNoFavoritePlace.visibility = View.GONE
                    binding.view2.visibility = View.INVISIBLE
                    binding.rvProfileFavoritePlace.visibility = View.VISIBLE
                }
                favoritePlaceAdapter.submitList(list)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.repTravelJournal.collectLatest { item ->
                if (item != null) {
                    binding.tvTabPlaceMyJourneyContent.text = item.content
                    binding.itemProfileRepTravelJournal.tvItemMyJournalTitle.text = item.title
                    binding.itemProfileRepTravelJournal.tvItemMyJournalDate.text =
                        binding.root.context.getString(
                            R.string.place_journey_date,
                            item.travelStartDate,
                            item.travelEndDate
                        )
                    binding.tvProfileNoRepJournal.visibility = View.GONE
                    binding.itemProfileRepTravelJournal.root.visibility = View.VISIBLE
                    binding.viewJournalMemoryDecorationElev2.visibility = View.VISIBLE
                    binding.viewTabPlaceMyJourney.visibility = View.VISIBLE
                    binding.layoutRepTravelJournal.setOnClickListener {
                        moveToJournalDetail(item.travelJournalId)
                    }
                } else {
                    binding.tvProfileNoRepJournal.visibility = View.VISIBLE
                    binding.itemProfileRepTravelJournal.root.visibility = View.GONE
                    binding.viewJournalMemoryDecorationElev2.visibility = View.GONE
                    binding.viewTabPlaceMyJourney.visibility = View.INVISIBLE
                }

                repJournalFriendAdapter.submitList(item?.travelCompanionSimpleResponses?.map { it.profileUrl })
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.bookMarkTravelJournals.collectLatest { list ->
                if (list.isEmpty()) {
                    binding.rvProfileBookmarkTravelJournal.visibility = View.INVISIBLE
                    binding.tvProfileNoBookmarkJournal.visibility = View.VISIBLE
                } else {
                    binding.rvProfileBookmarkTravelJournal.visibility = View.VISIBLE
                    binding.tvProfileNoBookmarkJournal.visibility = View.INVISIBLE
                }
                bookmarkJournalAdapter.submitList(list)
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
                    if (userInfo.userStatistics.travelJournalCount <= NO_TRAVEL_JOURNAL_COUNT) {
                        binding.layoutProfileNoTravellog.root.visibility = View.VISIBLE
                        binding.ivProfileImage.visibility = View.INVISIBLE
                        binding.tvProfileTotalTravelCount.visibility = View.INVISIBLE
                    } else {
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
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.journalInfo.collectLatest { info ->
                if (info != null) {
                    childFragmentManager.beginTransaction()
                        .add(
                            R.id.fragment_travel_journal_map,
                            TravelJournalMapFragment(
                                travelJournalInfo = info,
                                pinMode = PinMode.IMAGE_PIN,
                                isMapLine = true
                            )
                        )
                        .setReorderingAllowed(true)
                        .commit()

                    binding.itemProfileRepTravelJournal.btnItemMyJournalMoreFriend.isGone =
                        info.travelJournalCompanions.size < MAX_ABLE_SHOW_FRIENDS_NUM

                    binding.itemProfileRepTravelJournal.btnItemMyJournalMoreFriend.setOnClickListener {
                        if (togetherFriendBottomFragment == null) {
                            togetherFriendBottomFragment = TogetherFriendBottomFragment(info.travelJournalCompanions)

                        }
                        if (togetherFriendBottomFragment?.isAdded?.not() == true) {
                            togetherFriendBottomFragment?.show(
                                requireActivity().supportFragmentManager,
                                TogetherFriendBottomFragment.TAG,
                            )
                        }
                    }
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
            is MyProfileViewModel.Event.OnSelectRepJournal -> {
                val action = MyProfileFragmentDirections.actionFragmentMypageToRepTravelJournalFragment(event.selectRepTravelJournalDTO)
                findNavController().navigate(action)
            }

            else -> {}
        }
    }

    private fun moveToJournalDetail(travelId: Long){
        val action = MyProfileFragmentDirections.actionFragmentMypageToFragmentTravelJournal(travelId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.rvProfileLifeshot.removeOnScrollListener(infinityScrollListener)
        binding.rvProfileBookmarkTravelJournal.removeOnScrollListener(bookMarkJournalInfinityScrollListener)
        binding.rvProfileLifeshot.adapter = null
        binding.rvProfileFavoritePlace.adapter = null
        binding.rvProfileBookmarkTravelJournal.adapter = null
        super.onDestroyView()
    }

    companion object{
        const val DEFAULT_FAVORITE_PLACE_COUNT = 4
        const val NO_TRAVEL_JOURNAL_COUNT = 0
        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
    }
}
