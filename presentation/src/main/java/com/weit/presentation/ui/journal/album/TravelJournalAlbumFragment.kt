package com.weit.presentation.ui.journal.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalDetialModelBinding
import com.weit.presentation.model.journal.TravelJournalContentUpdateDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalAlbumFragment(
    private val travelJournalInfo: TravelJournalInfo,
    val moveToTravelJournalContentsUpdate: (TravelJournalContentUpdateDTO) -> Unit
): BaseFragment<ItemJournalDetialModelBinding>(
    ItemJournalDetialModelBinding::inflate
) {
    @Inject
    lateinit var viewModelFactory: TravelJournalAlbumViewModel.TravelJournalInfoFactory

    private val viewModel :TravelJournalAlbumViewModel by viewModels {
        TravelJournalAlbumViewModel.provideFactory(viewModelFactory, travelJournalInfo)
    }

    private val travelJournalAlbumAdapter: TravelJournalAlbumAdapter = TravelJournalAlbumAdapter(
        { updateContent(it) },
        { deleteContent(it) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTravelJournalAlbumRV()
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.journalContents.collectLatest { info ->
                travelJournalAlbumAdapter.submitList(info)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvItemJournalDetailModel.adapter = null
        super.onDestroyView()
    }

    private fun initTravelJournalAlbumRV() {
        binding.rvItemJournalDetailModel.apply {
            adapter = travelJournalAlbumAdapter
            addItemDecoration(SpaceDecoration(resources, bottomDP = R.dimen.item_memory_day_space))
        }
    }

    private fun updateContent(contentId: Long) {
        viewModel.updateTravelJournalContent(contentId)
    }

    private fun deleteContent(contentId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.journal_content_delete_title))
            .setMessage(getString(R.string.journal_delete_message))
            .setNegativeButton(getString(R.string.journal_delete_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.journal_delete_positive)) { dialog, _ ->
                viewModel.deleteContent(contentId)
                dialog.dismiss()
            }
            .show()
    }

    private fun handleEvent(event: TravelJournalAlbumViewModel.Event) {
        when (event) {
            is TravelJournalAlbumViewModel.Event.MoveToUpdate -> {
                moveToTravelJournalContentsUpdate(event.travelJournalContentUpdateDTO)
            }
            TravelJournalAlbumViewModel.Event.DeleteTravelJournalContent -> {
                sendSnackBar("여행일지 컨텐츠가 삭제되었습니다.")
            }
        }
    }
}
