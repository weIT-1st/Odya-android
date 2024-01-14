package com.weit.presentation.ui.profile.otherprofile

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFriendProfileBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.profile.bookmarkjournal.ProfileBookmarkJournalAdapter
import com.weit.presentation.ui.profile.otherprofile.favoriteplace.OtherFavoritePlaceAdapter
import com.weit.presentation.ui.profile.reptraveljournal.RepTravelJournalAdapter
import com.weit.presentation.ui.util.DimensionUtils
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

    private val repTravelJournalAdapter = RepTravelJournalAdapter(
        selectTravelJournal = { journal ->
            moveToJournalDetail(journal)
        }
    )

    private val bookmarkJournalAdapter = ProfileBookmarkJournalAdapter (
        showDetail = { moveToJournalDetail(it) },
        updateBookmarkState = { viewModel.updateBookmarkTravelJournalBookmarkState(it) }
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.rvProfileTravelJournal.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.main_my_journal_margin))
            addOnScrollListener(repInfinityScrollListener)
            adapter = repTravelJournalAdapter
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

    private val repInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextRepTravelJournals()
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
            viewModel.favoritePlaceCount.collectLatest { count ->
                if(count > 4){
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
            viewModel.repTravelJournals.collectLatest { list ->
                repTravelJournalAdapter.submitList(list)
            }
        }
        repeatOnStarted(viewLifecycleOwner){
            viewModel.bookMarkTravelJournals.collectLatest { list ->
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

                    binding.tvProfileTotalTravelCount.text = HtmlCompat.fromHtml(
                        baseString,
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                    )

                }
            }
        }
    }

    private fun moveToJournalDetail(travelId: Long){
//        val action = MemoryFragmentDirections.actionFragmentMemoryToFragmentTravelJournal(travelId)
//        findNavController().navigate(action)
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
        binding.rvProfileTravelJournal.adapter = null
        binding.rvProfileTravelJournal.removeOnScrollListener(repInfinityScrollListener)
        binding.rvProfileBookmarkTravelJournal.adapter = null
        binding.rvProfileBookmarkTravelJournal.removeOnScrollListener(bookMarkJournalInfinityScrollListener)
        super.onDestroyView()
    }
}
