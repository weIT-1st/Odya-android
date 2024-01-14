package com.weit.presentation.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.weit.domain.model.journal.TravelJournalListInfo
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostBinding
import com.weit.presentation.databinding.FragmentFeedPostTravellogBinding
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.feed.FeedTopic
import com.weit.presentation.model.feed.SelectTravelJournalDTO
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotImageDTO
import com.weit.presentation.model.profile.lifeshot.SelectLifeShotPlaceDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.feed.detail.CommentDialogFragment
import com.weit.presentation.ui.feed.detail.FeedDetailFragmentArgs
import com.weit.presentation.ui.feed.detail.FeedDetailViewModel
import com.weit.presentation.ui.feed.detail.menu.FeedDetailMyMenuFragment
import com.weit.presentation.ui.feed.post.FeedPostTopicAdapter
import com.weit.presentation.ui.feed.post.FeedPostViewModel
import com.weit.presentation.ui.feed.post.traveljournal.FeedTravelJournalAction
import com.weit.presentation.ui.feed.post.traveljournal.FeedTravelJournalAdapter
import com.weit.presentation.ui.feed.post.traveljournal.FeedTravelJournalDialogFragment
import com.weit.presentation.ui.feed.post.traveljournal.FeedTravelJournalViewModel
import com.weit.presentation.ui.post.selectplace.SelectPlaceAction
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerFragmentDirections
import com.weit.presentation.ui.profile.lifeshot.LifeShotPickerViewModel
import com.weit.presentation.ui.util.InfinityScrollListener
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FeedTravelJournalFragment : BaseFragment<FragmentFeedPostTravellogBinding>(
    FragmentFeedPostTravellogBinding::inflate,
) {

    private val feedTravelJournalAdapter = FeedTravelJournalAdapter { action ->
        handleAdapterAction(action)
    }

    private var feedTravelJournalDialog: FeedTravelJournalDialogFragment? = null

    private val args: FeedTravelJournalFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: FeedTravelJournalViewModel.FeedTravelJournalFactory

    private val viewModel: FeedTravelJournalViewModel by viewModels {
        FeedTravelJournalViewModel.provideFactory(viewModelFactory, args.journalId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initJournalRecyclerView()
    }

    private fun initJournalRecyclerView() {
        binding.rvFeedTravelLog.run {
            addOnScrollListener(infinityScrollListener)
            addItemDecoration(
                SpaceDecoration(
                    resources,
                    bottomDP = R.dimen.item_travel_journal_space,
                ),
            )
            adapter = feedTravelJournalAdapter
        }
    }

    private val infinityScrollListener by lazy {
        object : InfinityScrollListener() {
            override fun loadNextPage() {
                viewModel.onNextMyTravelJournals()
            }
        }
    }


    override fun initListener() {
        super.initListener()
        binding.ivFeedTravelLogClose.setOnClickListener { findNavController().popBackStack() }
        binding.ivFeedTravelLogCompelete.setOnClickListener {
            viewModel.onComplete()
        }
    }


    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.journals.collectLatest { journals ->
                feedTravelJournalAdapter.submitList(journals)
            }
        }

    }

    private fun handleAdapterAction(action: FeedTravelJournalAction) {
        when (action) {
            is FeedTravelJournalAction.OnClickPublicJournal -> {
                viewModel.onClickPublicJournal(action.journal)
            }

            is FeedTravelJournalAction.OnClickPrivateJournal -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.feed_travel_journal_visibility_title))
                    .setMessage(getString(R.string.feed_travel_journal_visibility_content))
                    .setNegativeButton(getString(R.string.feed_travel_journal_cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.feed_travel_journal_visibility_update)) { dialog, which ->
                        viewModel.updateTravelJournalVisibility(action.journal)
                    }
                    .show()
            }
        }
    }

    private fun handleEvent(event: FeedTravelJournalViewModel.Event) {
        when (event) {
            is FeedTravelJournalViewModel.Event.OnClickPublicJournalSuccess -> {
                binding.ivFeedTravelLogCompelete.visibility = View.VISIBLE
            }

            is FeedTravelJournalViewModel.Event.OnCompleted -> {
                backToPost(event.selectedTravelJournal)
            }
        }
    }

    private fun backToPost(selectedTravelJournal: TravelJournalListInfo?) {
        if (selectedTravelJournal != null) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "selectedTravelJournal",
                SelectTravelJournalDTO(
                    selectedTravelJournal.travelJournalId,
                    selectedTravelJournal.travelJournalTitle
                )
            )
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        binding.rvFeedTravelLog.removeOnScrollListener(infinityScrollListener)
        binding.rvFeedTravelLog.adapter = null
        super.onDestroyView()
    }
}
