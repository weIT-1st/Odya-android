package com.weit.presentation.ui.post.travellog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentPostTravelLogBinding
import com.weit.presentation.model.post.travellog.TravelPeriod
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.post.datepicker.DatePickerDialogFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PostTravelLogFragment : BaseFragment<FragmentPostTravelLogBinding>(
    FragmentPostTravelLogBinding::inflate,
) {

    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    private val args: PostTravelLogFragmentArgs by navArgs()

    private val viewModel: PostTravelLogViewModel by navGraphViewModels(R.id.post_graph) {
        defaultViewModelProviderFactory
    }

    private val dailyTravelLogAdapter = DailyTravelLogAdapter { action ->
        handleAdapterAction(action)
    }
    private val travelFriendsAdapter = TravelFriendsAdapter()

    private var datePickerDialog: DatePickerDialogFragment? = null
    private var dailyDatePickerDialog: DatePickerDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvPostTravelLogDaily.adapter = dailyTravelLogAdapter
        binding.includePostTravelLogFriends.rvTravelFriends.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_travel_friends_space))
            adapter = travelFriendsAdapter
        }
        viewModel.initViewState(args.followers?.toList(), args.selectPlace)
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
    }

    override fun initCollector() {
        super.initCollector()
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.dailyTravelLogs.collectLatest { logs ->
                dailyTravelLogAdapter.submitList(logs)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.travelFriendsInfo.collectLatest { info ->
                travelFriendsAdapter.submitList(info.friendsSummary)
                if (info.remainingFriendsCount > 0) {
                    binding.includePostTravelLogFriends.tvTravelFriendsCount.text =
                        getString(R.string.post_travel_log_friends_count, info.remainingFriendsCount)
                }
            }
        }
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
    }

    private fun handleEvent(event: PostTravelLogViewModel.Event) {
        when (event) {
            is PostTravelLogViewModel.Event.OnEditTravelFriends -> {
                val action = PostTravelLogFragmentDirections.actionPostTravelLogFragmentToTravelFriendFragment(
                    event.travelFriends.toTypedArray(),
                )
                findNavController().navigate(action)
            }
            is PostTravelLogViewModel.Event.OnSelectPlace -> {
                val action = PostTravelLogFragmentDirections.actionPostTravelLogFragmentToSelectPlaceFragment(
                    event.imagePlaces.toTypedArray(),
                )
                findNavController().navigate(action)
            }
            is PostTravelLogViewModel.Event.ShowDatePicker -> {
                showDatePickerDialog(event.currentPeriod)
            }
            PostTravelLogViewModel.Event.ClearDatePickerDialog -> {
                datePickerDialog = null
                dailyDatePickerDialog = null
            }
            is PostTravelLogViewModel.Event.ShowDailyDatePicker -> {
                showDailyDatePickerDialog(
                    event.position,
                    event.currentDate,
                    event.minDateMillis,
                    event.maxDateMillis,
                )
            }
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

    private fun showDailyDatePickerDialog(
        position: Int,
        currentDate: LocalDate?,
        minDateMillis: Long,
        maxDateMillis: Long,
    ) {
        if (dailyDatePickerDialog == null) {
            dailyDatePickerDialog = getDailyDatePickerDialog(position, currentDate, minDateMillis, maxDateMillis)
        }
        if (dailyDatePickerDialog?.isShowing == false) {
            dailyDatePickerDialog?.show()
        }
    }

    private fun getDailyDatePickerDialog(
        position: Int,
        initDate: LocalDate?,
        minDateMillis: Long,
        maxDateMillis: Long,
    ): DatePickerDialog {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.onDailyDateSelected(position, year, month, dayOfMonth)
        }
        return if (initDate != null) {
            DatePickerDialog(
                requireContext(),
                listener,
                initDate.year,
                initDate.monthValue,
                initDate.dayOfMonth,
            )
        } else {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener(listener)
            }
        }.apply {
            datePicker.minDate = minDateMillis
            datePicker.maxDate = maxDateMillis
            setOnDismissListener { viewModel.onDatePickerDismissed() }
        }
    }

    private fun handleAdapterAction(action: DailyTravelLogAction) {
        when (action) {
            is DailyTravelLogAction.OnDeletePicture -> {
                viewModel.onDeletePicture(action.position, action.imageIndex)
            }
            is DailyTravelLogAction.OnSelectPicture -> {
                viewModel.onSelectPictures(action.position, pickImageUseCase)
            }
            is DailyTravelLogAction.OnSelectPlace -> {
                viewModel.onSelectPlace(action.position)
            }
            is DailyTravelLogAction.OnDeleteDailyTravelLog -> {
                viewModel.onDeleteDailyTravelLog(action.position)
            }
            is DailyTravelLogAction.OnPickDate -> {
                viewModel.onPickDailyDate(action.position)
            }
        }
    }

    private fun LocalDate.toDateString(): String {
        return getString(
            R.string.edit_text_birth,
            year,
            monthValue,
            dayOfMonth,
        )
    }

    override fun onDestroyView() {
        binding.includePostTravelLogFriends.rvTravelFriends.adapter = null
        binding.rvPostTravelLogDaily.adapter = null
        super.onDestroyView()
    }
}
