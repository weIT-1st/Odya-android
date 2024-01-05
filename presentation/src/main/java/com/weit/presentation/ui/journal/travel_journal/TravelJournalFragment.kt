package com.weit.presentation.ui.journal.travel_journal

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelJournalBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.journal.detail.TravelJournalDetailFragment
import com.weit.presentation.ui.journal.menu.TravelJournalDetailMenuFragment
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
    private var travelJournalDetailMenuFragment: TravelJournalDetailMenuFragment? = null

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

        repeatOnStarted(viewLifecycleOwner){
            viewModel.travelJournalDetailToolBarInfo.collectLatest { info ->
                initMenu(info)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun initMenu(info: TravelJournalViewModel.TravelJournalDetailToolBarInfo) {
        if (info.isMyTravelJournal) {
            binding.tbTravelJournal.title = info.title
            binding.tbTravelJournal.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_journal_bookmark -> {
                        // todo bookmark 처리
                    }
                    R.id.menu_journal_alert -> {
                        viewModel.popUpJournalMenu()
                    }
                }
                true
            }
        } else {
            binding.tbTravelJournal.title = info.title
            binding.tbTravelJournal.menu.clear()
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

    private fun popUpJournalMenuBottomSheet() {
        if (travelJournalDetailMenuFragment == null) {
            travelJournalDetailMenuFragment = TravelJournalDetailMenuFragment(
                { viewModel.moveToJournalUpdate() },
                { viewModel.deleteTravelJournal() }
            )
        }

        if ( !travelJournalDetailFragment!!.isAdded) {
            travelJournalDetailMenuFragment!!.show(childFragmentManager, TAG)
        }
    }

    private fun handleEvent(event: TravelJournalViewModel.Event) {
        when (event) {
            is TravelJournalViewModel.Event.MoveToJournalUpdate -> {
                travelJournalDetailFragment = null

                val action = TravelJournalFragmentDirections.actionFragmentTravelJournalToTravelJournalUpdateFragment(event.travelJournalUpdateDTO)
                findNavController().navigate(action)
            }
            is TravelJournalViewModel.Event.PopupTravelJournalMenu -> {
                popUpJournalMenuBottomSheet()
            }

            TravelJournalViewModel.Event.DeleteTravelJournalSuccess -> {
                sendSnackBar("여행일지가 삭제되었습니다.")
            }
        }
    }

    companion object {
        private const val TAG = "TravelJournalFragment"
    }
}
