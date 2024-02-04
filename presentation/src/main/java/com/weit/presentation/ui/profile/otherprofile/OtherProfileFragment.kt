package com.weit.presentation.ui.profile.otherprofile

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFriendProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.profile.bookmarkjournal.ProfileBookmarkJournalAdapter
import com.weit.presentation.ui.profile.otherprofile.favoriteplace.OtherFavoritePlaceAdapter
import com.weit.presentation.ui.profile.reptraveljournal.RepTravelJournalFriendAdapter
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
    private val favoritePlaceAdapter = OtherFavoritePlaceAdapter(
        selectPlace = { place ->
            viewModel.selectFavoritePlace(place)
        }
    )
    private val repJournalFriendAdapter = RepTravelJournalFriendAdapter()

    private val bookmarkJournalAdapter = ProfileBookmarkJournalAdapter (
        showDetail = { moveToJournalDetail(it) },
        updateBookmarkState = { viewModel.updateBookmarkTravelJournalBookmarkState(it) }
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName = arguments?.getString("userName")
        viewModel.initialize(userName)
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
        binding.itemProfileRepTravelJournal.rvItemMyJournalFriends.run {
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    rightDP = R.dimen.item_journal_friends_space,
                ),
            )
            adapter = repJournalFriendAdapter
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

    override fun initListener() {
        binding.btnProfileFavoritePlaceMore.setOnClickListener {
            val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFragmentMap()
            findNavController().navigate(action)
        }
        binding.btProfileFriendFollow.setOnClickListener {
            viewModel.onFollowStateChange()
        }
        binding.ivProfileBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.viewProfileTotalCount.setOnClickListener {
            viewModel.goToFriendManage()
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
                if (lifeshots.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(lifeshots.first().imageUrl)
                        .into(binding.ivProfileBg)
                    binding.tvProfileNoLifeShot.visibility = View.GONE
                    binding.rvProfileLifeshot.visibility = View.VISIBLE
                }else{
                    binding.tvProfileNoLifeShot.visibility = View.VISIBLE
                    binding.rvProfileLifeshot.visibility = View.INVISIBLE
                }
                    otherProfileLifeShotAdapter.submitList(lifeshots)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.favoritePlaceCount.collectLatest { count ->
                if(count > DEFAULT_FAVORITE_PLACE_COUNT){
                    binding.btnProfileFavoritePlaceMore.text = getString(
                        R.string.profile_bookmark_place,
                        count-4
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
                    binding.rvProfileFavoritePlace.visibility = View.INVISIBLE
                }else{
                    binding.tvProfileNoFavoritePlace.visibility = View.GONE
                    binding.rvProfileFavoritePlace.visibility = View.VISIBLE
                }
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
            viewModel.repTravelJournal.collectLatest { item ->
                if (item != null) {
                    binding.tvTabPlaceMyJourneyContent.text = item.content
                    binding.itemProfileRepTravelJournal.tvItemMyJournalTitle.text = item.title
                    binding.itemProfileRepTravelJournal.tvItemMyJournalDate.text = binding.root.context.getString(R.string.place_journey_date, item.travelStartDate, item.travelEndDate)
                    binding.tvProfileNoRepJournal.visibility = View.GONE
                    binding.itemProfileRepTravelJournal.root.visibility = View.VISIBLE
                    binding.viewJournalMemoryDecorationElev2.visibility = View.VISIBLE
                    binding.viewTabPlaceMyJourney.visibility = View.VISIBLE
                    binding.layoutRepTravelJournal.setOnClickListener {
                        moveToJournalDetail(item.travelJournalId)
                    }
                }else{
                    binding.tvProfileNoRepJournal.visibility = View.VISIBLE
                    binding.itemProfileRepTravelJournal.root.visibility = View.GONE
                    binding.viewJournalMemoryDecorationElev2.visibility = View.GONE
                    binding.viewTabPlaceMyJourney.visibility = View.INVISIBLE
                }
                binding.itemProfileRepTravelJournal.btnItemMyJournalMoreFriend.isVisible = item?.travelCompanionSimpleResponses != null
                //TODO 더보기 눌렀을 때 바텀시트 올라오기

                repJournalFriendAdapter.submitList(item?.travelCompanionSimpleResponses?.map{it.profileUrl})
            }
        }
        repeatOnStarted(viewLifecycleOwner){
            viewModel.bookMarkTravelJournals.collectLatest { list ->
                if(list.isEmpty()){
                    binding.rvProfileBookmarkTravelJournal.visibility = View.INVISIBLE
                    binding.tvProfileNoBookmarkJournal.visibility = View.VISIBLE
                }else{
                    binding.rvProfileBookmarkTravelJournal.visibility = View.VISIBLE
                    binding.tvProfileNoBookmarkJournal.visibility = View.INVISIBLE
                }
                bookmarkJournalAdapter.submitList(list)            }
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
                    if(userInfo.userStatistics.travelJournalCount <= NO_TRAVEL_JOURNAL_COUNT){
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
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.journalInfo.collectLatest { info ->
                if (info != null) {
                    childFragmentManager.beginTransaction()
                        .add(
                            com.weit.presentation.R.id.fragment_travel_journal_map,
                            com.weit.presentation.ui.journal.map.TravelJournalMapFragment(
                                travelJournalInfo = info,
                                pinMode = com.weit.presentation.ui.journal.map.PinMode.IMAGE_PIN,
                                isMapLine = true
                            )
                        )
                        .setReorderingAllowed(true)
                        .commit()
                }
            }
        }
    }

    private fun moveToJournalDetail(travelId: Long){
        val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFragmentTravelJournal(travelId)
        findNavController().navigate(action)
    }

    private fun handleEvent(event: OtherProfileViewModel.Event) {

        when (event) {
            is OtherProfileViewModel.Event.GoToFriendManage -> {
                val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToOtherFriendManageFragment(event.userId)
                findNavController().navigate(action)
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        binding.rvProfileLifeshot.removeOnScrollListener(infinityScrollListener)
        binding.rvProfileLifeshot.adapter = null
        binding.rvProfileFavoritePlace.adapter = null
        binding.rvProfileBookmarkTravelJournal.adapter = null
        binding.rvProfileBookmarkTravelJournal.removeOnScrollListener(bookMarkJournalInfinityScrollListener)
        super.onDestroyView()
    }

    companion object{
        const val DEFAULT_FAVORITE_PLACE_COUNT = 4
        const val NO_TRAVEL_JOURNAL_COUNT = 0
    }
}
