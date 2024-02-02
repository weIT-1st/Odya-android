package com.weit.presentation.ui.journal.update.journal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentUpdateTravelLogBinding
import com.weit.presentation.model.Visibility
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.post.datepicker.DatePickerDialogFragment
import com.weit.presentation.ui.post.travellog.TravelFriendsAdapter
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalUpdateFragment : BaseFragment<FragmentUpdateTravelLogBinding>(
    FragmentUpdateTravelLogBinding::inflate
) {
    @Inject
    lateinit var viewModelFactory: TravelJournalUpdateViewModel.TravelJournalUpdateFactory

    private val args: TravelJournalUpdateFragmentArgs by navArgs()
    private val viewModel : TravelJournalUpdateViewModel by viewModels {
        TravelJournalUpdateViewModel.provideFactory(viewModelFactory, args.travelJounalUpdateDTO)
    }

    private var datePickerDialog: DatePickerDialogFragment? = null
    private var dailyDatePickerDialog: DatePickerDialog? = null

    private val travelFriendsAdapter = TravelFriendsAdapter()

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            viewModel.selectTravelLogVisibility(Visibility.fromPosition(tab.position))
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            // 비워둠
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            // 비워둠
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.etPostTravelLogName.hint = args.travelJounalUpdateDTO.title
        initRecyclerView()
    }

    override fun initListener() {
        binding.includePostTravelLogFriends.btnTravelFriendsAdd.setOnClickListener {
            viewModel.onEditTravelFriends()
        }

        binding.includePostTravelLogStart.root.setOnClickListener {
            viewModel.showDatePicker()
        }

        binding.includePostTravelLogEnd.root.setOnClickListener {
            viewModel.showDatePicker()
        }

        binding.tbPostTravelLog.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.travelPeriod.collectLatest { period ->
                binding.includePostTravelLogStart.run {
                    tvDatePickerDate.text = period.start.toDateString()
                    tvDatePickerDayOfWeek.text = period.start.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                }
                binding.includePostTravelLogEnd.run {
                    tvDatePickerDate.text = period.end.toDateString()
                    tvDatePickerDayOfWeek.text = period.end.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.travelFriendsInfo.collectLatest { info ->
                travelFriendsAdapter.submitList(info.friendsSummary)
                if (info.remainingFriendsCount > 0) {
                    binding.includePostTravelLogFriends.tvTravelFriendsCount.text = getString(R.string.post_travel_log_friends_count, info.remainingFriendsCount)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun initRecyclerView() {
        binding.includePostTravelLogFriends.rvTravelFriends.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_travel_friends_space))
            adapter = travelFriendsAdapter
        }
    }

    private fun showDatePickerDialog(period: TravelPeriod) {
        if (datePickerDialog == null) {
            datePickerDialog = DatePickerDialogFragment(
                travelPeriod = period,
                onComplete = { viewModel.onChangePeriod(it) },
                onDismiss = { viewModel.onDatePickerDismissed() },
            )
        }
        if (datePickerDialog?.isAdded == false) {
            datePickerDialog?.show(childFragmentManager, null)
        }
    }

    private fun handleEvent(event: TravelJournalUpdateViewModel.Event) {
        when (event) {
            is TravelJournalUpdateViewModel.Event.OnEditTravelFriends -> {
                // todo 프레그먼트간 이동
            }
            is TravelJournalUpdateViewModel.Event.ShowDataPicker -> {
                showDatePickerDialog(event.currentPeriod)
            }
            TravelJournalUpdateViewModel.Event.IsBlankTitle -> {
                sendSnackBar("여행일지 제목을 입력해주세요")
            }
            TravelJournalUpdateViewModel.Event.ClearDatePickerDialog -> {
                datePickerDialog = null
                dailyDatePickerDialog = null
            }
        }
    }

    private fun LocalDate.toDateString(): String {
        return getString(R.string.edit_text_birth, year, monthValue, dayOfMonth)
    }

}
