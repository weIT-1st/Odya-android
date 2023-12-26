package com.weit.presentation.ui.journal.travel_journal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.weit.presentation.databinding.FragmentTravelJournalBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TravelJournalFragment : BaseFragment<FragmentTravelJournalBinding>(
    FragmentTravelJournalBinding::inflate,
) {
    private val args: TravelJournalFragmentArgs by navArgs()
    private val viewModel: TravelJournalViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        Logger.t("MainTest").i("TravelLogFragment에서 받은 args${args.travelJournalId}")
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.journalImage.collectLatest {
            }
        }
    }
}
