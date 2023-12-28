package com.weit.presentation.ui.journal.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.BottomSheetTravelJournalDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalDetailFragment(
    private val travelJournalInfo: TravelJournalInfo
): BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: TravelJournalDetailViewModel.TravelJournalInfoFactory

    private val viewModel: TravelJournalDetailViewModel by viewModels {
        TravelJournalDetailViewModel.provideFactory(viewModelFactory, travelJournalInfo)
    }

    private var _binding: BottomSheetTravelJournalDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetTravelJournalDetailBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initJournalModelViewPager()

        binding.tvJournalDetailMainTitle.text = viewModel.journalInfo.travelJournalTitle
        binding.tvJournalDetailTravelDate.text =
            requireContext().getString(R.string.journal_memory_my_travel_date, viewModel.journalInfo.travelStartDate, viewModel.journalInfo.travelEndDate)
        binding.btnJournalDetailMoreFriends.isGone = viewModel.journalInfo.travelJournalCompanions.size < 3

    }

    private fun initFriendRV(){
        binding.rvJournalDetailFriends.adapter
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initJournalModelViewPager(){
        val viewPager = binding.viewPagerJournalDetail
        val tabLayout = binding.tlJournalDetailChooseModel

        viewPager.apply {
            adapter = TravelJournalModelAdapter(childFragmentManager, lifecycle, viewModel.journalInfo)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when(position) {
                0 -> { tab.icon = requireContext().getDrawable(R.drawable.ic_journal_basic) }
                else -> { tab.icon = requireContext().getDrawable(R.drawable.ic_bento_menu)}
            }
        }.attach()
    }
}
