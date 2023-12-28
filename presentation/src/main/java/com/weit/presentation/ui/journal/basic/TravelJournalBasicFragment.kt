package com.weit.presentation.ui.journal.basic

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.databinding.ItemJournalDetialModelBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalBasicFragment(
    private val travelJournalInfo: TravelJournalInfo
): BaseFragment<ItemJournalDetialModelBinding>(
    ItemJournalDetialModelBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: TravelJournalBasicViewModel.TravelJournalInfoFactory

    private val viewModel: TravelJournalBasicViewModel by viewModels {
        TravelJournalBasicViewModel.provideFactory(viewModelFactory, travelJournalInfo)
    }

    private val travelJournalBasicAdapter: TravelJournalBasicAdapter = TravelJournalBasicAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTravelJournalBasicRV()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.journalContents.collectLatest {info ->
                travelJournalBasicAdapter.submitList(info)
            }
        }
    }

    private fun initTravelJournalBasicRV(){
        binding.rvItemJournalDetailModel.adapter = travelJournalBasicAdapter
    }
}
