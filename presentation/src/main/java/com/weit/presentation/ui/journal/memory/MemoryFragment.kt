package com.weit.presentation.ui.journal.memory

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.weit.domain.model.bookmark.JournalBookMarkInfo
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.model.place.PlaceMyReviewInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMemoryBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.journal.memory.adapter.BookmarkJournalAdapter
import com.weit.presentation.ui.journal.memory.adapter.MyJournalAdapter
import com.weit.presentation.ui.journal.memory.adapter.MyReviewAdapter
import com.weit.presentation.ui.journal.memory.adapter.TaggedJournalAdapter
import com.weit.presentation.ui.journal.memory.viewmodel.MemoryReviewViewModel
import com.weit.presentation.ui.journal.memory.viewmodel.MyJournalViewModel
import com.weit.presentation.ui.journal.memory.viewmodel.OtherJournalViewModel
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MemoryFragment : BaseFragment<FragmentMemoryBinding>(
    FragmentMemoryBinding::inflate,
) {

    private val myJournalViewModel: MyJournalViewModel by viewModels()
    private val otherJournalViewModel: OtherJournalViewModel by viewModels()
    private val reviewViewModel: MemoryReviewViewModel by viewModels()

    private val myJournalAdapter = MyJournalAdapter(
        { journalId -> myJournalViewModel.moveToJournal(journalId) },
        { journalId -> myJournalViewModel.updateTravelJournalBookmarkState(journalId) }
        )
    private val bookmarkJournalAdapter = BookmarkJournalAdapter (
        { journalId -> moveToJournalDetail(journalId) },
        { journalId -> otherJournalViewModel.updateBookmarkTravelJournalBookmarkState(journalId) }
    )
    private val taggedJournalAdapter = TaggedJournalAdapter(
        { journalId -> moveToJournalDetail(journalId) },
        { journalId -> otherJournalViewModel.updateTaggedTravelJournalBookmarkState(journalId) },
        { otherJournalViewModel.deleteTaggedJournal() }
    )
    private val myReviewAdapter = MyReviewAdapter { reviewViewModel.deleteReview(it) }

    private val myJournalInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                myJournalViewModel.onNextJournal()
            }
        }
    }

    private val bookMarkJournalInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                otherJournalViewModel.onNextBookMarkJournal()
            }
        }
    }

    private val taggedJournalInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                otherJournalViewModel.onNextTaggedJournal()
            }
        }
    }

    private val myReviewInfinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                reviewViewModel.onNextReview()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vmOther = otherJournalViewModel
        binding.vmReview = reviewViewModel

        initMyJournalRV()
        initBookmarkJournalRV()
        initTaggedJournalRV()
        initMyReviewsRV()
    }

    override fun initListener() {
        binding.includeJournalMemoryLastJournal.root.setOnClickListener {
            myJournalViewModel.moveToRandomJournal()
        }

        binding.btnTravelLogWrite.setOnClickListener {
           moveToPostTravelJournal()
        }

        binding.includeJournalMemoryNoJournal.btnFeedNoTravelLogWrite.setOnClickListener {
            moveToPostTravelJournal()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.isEmptyMyJournal.collectLatest {isEmpty ->
                binding.ivJournalMemoryMyProfile.isGone = isEmpty
                binding.includeJournalMemoryNoJournal.tvFeedNoTravelLogHint2.isGone = true
                binding.btnTravelLogWrite.isGone = isEmpty
                binding.lyJournalMemoryDecoration.isGone = isEmpty
                binding.includeJournalMemoryNoJournal.root.isGone = !isEmpty
                binding.tvJournalMemory.isGone = isEmpty
                binding.includeJournalMemoryLastJournal.root.isGone = isEmpty
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.myProfile.collectLatest {
                binding.tvJournalMemory.text = requireContext().getString(R.string.journal_memory_last_travel, it?.nickname)
                binding.user = it
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.randomJournal.collectLatest {
                if (it != null) {
                    Glide.with(requireContext())
                        .load(it.contentImageUrl)
                        .into(binding.includeJournalMemoryLastJournal.ivItemJournalMemoryLastJournal)

                    binding.includeJournalMemoryLastJournal.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = it.travelJournalTitle
                    binding.includeJournalMemoryLastJournal.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxPlace.text = it.placeDetail.firstOrNull()?.name ?: ""
                    binding.includeJournalMemoryLastJournal.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text = requireContext().getString(R.string.journal_memory_my_travel_date, it.travelStartDate, it.travelEndDate)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.myJournals.collectLatest {
                binding.tvJournalMemoryMyJournal.isGone = (it == emptyList<TravelJournalListInfo>())
                myJournalAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            otherJournalViewModel.bookMarkTravelJournals.collectLatest {
                binding.tvJournalMemoryBookmarkJournal.isGone = (it == emptyList<JournalBookMarkInfo>())
                bookmarkJournalAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            otherJournalViewModel.taggedTravelJournals.collectLatest{
                binding.tvJournalMemoryTagJournal.isGone = (it == emptyList<TravelJournalListInfo>())
                taggedJournalAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            reviewViewModel.myReviews.collectLatest {
                binding.tvJournalMemoryMyReview.isGone = (it == emptyList<PlaceMyReviewInfo>())
                myReviewAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvJournalMemoryBookmarkJournal.adapter = null
        binding.rvJournalMemoryBookmarkJournal.removeOnScrollListener(bookMarkJournalInfinityScrollListener)
        binding.rvJournalMemoryTagJournal.adapter = null
        binding.rvJournalMemoryTagJournal.removeOnScrollListener(taggedJournalInfinityScrollListener)
        binding.rvJournalMemoryMyReview.adapter = null
        binding.rvJournalMemoryMyReview.removeOnScrollListener(myReviewInfinityScrollListener)
        super.onDestroyView()
    }

    private fun initMyJournalRV() {
        binding.rvJournalMemoryMyJournal.run {
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_memory_all_space))
            addOnScrollListener(myJournalInfinityScrollListener)
            adapter = myJournalAdapter
        }
    }

    private fun initBookmarkJournalRV() {
        binding.rvJournalMemoryBookmarkJournal.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_memory_all_space))
            addOnScrollListener(bookMarkJournalInfinityScrollListener)
            adapter = bookmarkJournalAdapter
        }
    }

    private fun initTaggedJournalRV(){
        binding.rvJournalMemoryTagJournal.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_memory_all_space))
            addOnScrollListener(taggedJournalInfinityScrollListener)
            adapter = taggedJournalAdapter
        }
    }

    private fun initMyReviewsRV(){
        binding.rvJournalMemoryMyReview.run {
            addOnScrollListener(myReviewInfinityScrollListener)
            adapter = myReviewAdapter
        }

    }

    private fun moveToJournalDetail(travelId: Long){
        val action = MemoryFragmentDirections.actionFragmentMemoryToFragmentTravelJournal(travelId)
        findNavController().navigate(action)
    }

    private fun moveToPostTravelJournal() {
        val action = MemoryFragmentDirections.actionFragmentMemoryToPostGraph()
        findNavController().navigate(action)
    }

    private fun handelEvent(event: MyJournalViewModel.Event){
        when (event){
            is MyJournalViewModel.Event.MoveToRandomJournal -> {
                moveToJournalDetail(event.randomJournalId)
            }

            is MyJournalViewModel.Event.MoveToJournal -> {
                moveToJournalDetail(event.travelJournalId)
            }
        }
    }
}
