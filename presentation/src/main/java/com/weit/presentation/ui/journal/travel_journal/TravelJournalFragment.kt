package com.weit.presentation.ui.journal.travel_journal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.databinding.FragmentTravelJournalBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.journal.detail.TravelJournalDetailFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalFragment : BaseMapFragment<FragmentTravelJournalBinding>(
    FragmentTravelJournalBinding::inflate,
    FragmentTravelJournalBinding::mapTravelJournal
) {
    @Inject
    lateinit var viewModelFactory: TravelJournalViewModel.TravelJournalIdFactory

    private val args: TravelJournalFragmentArgs by navArgs()
    private val viewModel: TravelJournalViewModel by viewModels{
        TravelJournalViewModel.provideFactory(viewModelFactory, args.travelJournalId)
    }

    private var travelJournalDetailFragment: TravelJournalDetailFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.tbTravelJournal.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.journalInfo.collectLatest { info ->
                if (info != null){
                    popUpJournalDetailBottomSheet(info)
                    binding.tbTravelJournal.title = info.writer.nickname
                }
            }
        }
    }

    private fun popUpJournalDetailBottomSheet(info: TravelJournalInfo){
        if (travelJournalDetailFragment == null) {
            travelJournalDetailFragment = TravelJournalDetailFragment(info)
        }

        if (!travelJournalDetailFragment!!.isAdded) {
            travelJournalDetailFragment!!.show(childFragmentManager, TAG)
        }
    }

    companion object {
        private val TAG = "TravelJournalFragment"
    }
}
