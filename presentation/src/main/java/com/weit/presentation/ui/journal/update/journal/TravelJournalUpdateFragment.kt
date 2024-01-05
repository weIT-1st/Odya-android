package com.weit.presentation.ui.journal.update.journal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentPostTravelLogBinding
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
class TravelJournalUpdateFragment : BaseFragment<FragmentPostTravelLogBinding>(
    FragmentPostTravelLogBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: TravelJournalUpdateViewModel.TravelJournalUpdateFactory

    private val args: TravelJournalUpdateFragmentArgs by navArgs()
    private val viewModel : TravelJournalUpdateViewModel by viewModels {
        TravelJournalUpdateViewModel.provideFactory(viewModelFactory, args.travelJounalUpdateDTO)
    }

    private val travelFriendsAdapter = TravelFriendsAdapter()

    private var datePickerDialog: DatePickerDialogFragment? = null
    private var dailyDatePickerDialog: DatePickerDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includePostTravelLogFriends.rvTravelFriends.run {
            addItemDecoration(SpaceDecoration(resources, rightDP = R.dimen.item_travel_friends_space))
            adapter = travelFriendsAdapter

            binding.etPostTravelLogName.hint = viewModel.travelJournalUpdateDTO.title
        }
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

//        repeatOnStarted(viewLifecycleOwner) {
//            viewModel.travelFriendsInfo.collectLatest { info ->
//                travelFriendsAdapter.submitList(info.friendsSummary)
//                if (info.remainingFriendsCount > 0) {
//                    binding.includePostTravelLogFriends.tvTravelFriendsCount.text =
//                        getString(R.string.post_travel_log_friends_count, info.remainingFriendsCount)
//                }
//            }
//        }
    }

    private fun LocalDate.toDateString(): String {
        return getString(R.string.edit_text_birth, year, monthValue, dayOfMonth)
    }

}
