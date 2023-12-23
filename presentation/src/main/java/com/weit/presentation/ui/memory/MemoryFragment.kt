package com.weit.presentation.ui.memory

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMemoryBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.memory.adapter.BookmarkJournalAdapter
import com.weit.presentation.ui.memory.adapter.MyJournalAdapter
import com.weit.presentation.ui.memory.adapter.MyReviewAdapter
import com.weit.presentation.ui.memory.adapter.TaggedJournalAdapter
import com.weit.presentation.ui.memory.viewmodel.MemoryReviewViewModel
import com.weit.presentation.ui.memory.viewmodel.MyJournalViewModel
import com.weit.presentation.ui.memory.viewmodel.OtherJournalViewModel
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

    private val myJournalAdapter = MyJournalAdapter()
    private val bookmarkJournalAdapter = BookmarkJournalAdapter()
    private val taggedJournalAdapter = TaggedJournalAdapter()
    private val myReviewAdapter = MyReviewAdapter { reviewViewModel.deleteReview() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vmOther = otherJournalViewModel
        binding.vmReview = reviewViewModel

        initMyJournalRV()
        initBookmarkJournalRV()
        initBookmarkJournalRV()
        initTaggedJournalRV()
        initMyReviewsRV()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.isEmptyMyJournal.collectLatest {isEmpty ->
                binding.includeJournalMemoryNoJournal.root.isGone = !isEmpty
                binding.includeJournalMemoryNoJournal.tvFeedNoTravelLogHint2.isGone = true
                binding.tvJournalMemory.isGone = isEmpty
                binding.includeJournalMemoryLastJournal.root.isGone = isEmpty
                binding.tvJournalMemoryMyJournal.isGone = isEmpty
                binding.tvJournalMemoryMyJournal.isGone = isEmpty
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.randomJournal.collectLatest {
                if (it != null) {
                    Glide.with(requireContext())
                        .load(it.contentImageUrl)
                        .into(binding.includeJournalMemoryLastJournal.ivItemJournalMemoryLastJournal)

                    binding.includeJournalMemoryLastJournal.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxTitle.text = it.travelJournalTitle
                    binding.includeJournalMemoryLastJournal.includeItemJournalMemoryDetail.tvJournalMemoryDetailBoxDate.text = requireContext().getString(R.string.journal_memory_my_travel_date, it.travelStartDate, it.travelEndDate)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.myNickname.collectLatest {
                binding.tvJournalMemory.text = requireContext().getString(R.string.journal_memory_last_travel, it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.myProfile.collectLatest {
                if (it != null){
                    Glide.with(requireContext())
                        .load(it)
                        .into(binding.ivJournalMemoryMyProfile)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            myJournalViewModel.myJournals.collectLatest {
                myJournalAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            otherJournalViewModel.bookMarkTravelJournals.collectLatest {
                bookmarkJournalAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            otherJournalViewModel.taggedTravelJournals.collectLatest{
                taggedJournalAdapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            reviewViewModel.myReviews.collectLatest {
                myReviewAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvJournalMemoryBookmarkJournal.adapter = null
        binding.rvJournalMemoryTagJournal.adapter = null
        binding.rvJournalMemoryMyReview.adapter = null
        super.onDestroyView()
    }

    private fun initMyJournalRV() {
        binding.rvJournalMemoryMyReview.adapter = myJournalAdapter
    }

    private fun initBookmarkJournalRV() {
        binding.rvJournalMemoryBookmarkJournal.adapter = bookmarkJournalAdapter
    }

    private fun initTaggedJournalRV(){
        binding.rvJournalMemoryTagJournal.adapter = taggedJournalAdapter
    }

    private fun initMyReviewsRV(){
        binding.rvJournalMemoryMyReview.adapter = myReviewAdapter
    }
}
