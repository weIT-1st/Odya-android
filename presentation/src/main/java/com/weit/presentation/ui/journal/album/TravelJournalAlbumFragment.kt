package com.weit.presentation.ui.journal.album

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.ItemJournalDetialModelBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalAlbumFragment(
    private val travelJournalInfo: TravelJournalInfo
): BaseFragment<ItemJournalDetialModelBinding>(
    ItemJournalDetialModelBinding::inflate
) {
    @Inject
    lateinit var viewModelFactory: TravelJournalAlbumViewModel.TravelJournalInfoFactory

    private val viewModel :TravelJournalAlbumViewModel by viewModels {
        TravelJournalAlbumViewModel.provideFactory(viewModelFactory, travelJournalInfo)
    }

    private val travelJournalAlbumAdapter: TravelJournalAlbumAdapter = TravelJournalAlbumAdapter()

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
}
