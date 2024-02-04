package com.weit.presentation.ui.journal.update.content

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.weit.domain.usecase.image.PickImageUseCase
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentUpdateJournalContentsBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.post.travellog.DailyTravelLogAction
import com.weit.presentation.ui.post.travellog.DailyTravelLogPictureAdapter
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalContentUpdateFragment : BaseFragment<FragmentUpdateJournalContentsBinding>(
    FragmentUpdateJournalContentsBinding::inflate

) {
    @Inject
    lateinit var pickImageUseCase: PickImageUseCase

    @Inject
    lateinit var viewModelFactory : TravelJournalContentUpdateViewModel.TravelJournalContentUpdateFactory

    private val args : TravelJournalContentUpdateFragmentArgs by navArgs()
    private val viewModel: TravelJournalContentUpdateViewModel by viewModels {
        TravelJournalContentUpdateViewModel.provideFactory(
            viewModelFactory,
            args.travelJournalUpdateContentDTO
        )
    }

    private val pictureAdapter = DailyTravelLogPictureAdapter { position ->
        deleteSelectedPicture(position)
    }
    private var dailyDatePickerDialog: DatePickerDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel
        binding.etUpdateJournalContents.hint = args.travelJournalUpdateContentDTO.content ?: getString(R.string.post_travel_log_daily_contents_hint)
        binding.rvUpdateJournalContentPictures.adapter = pictureAdapter
    }

    override fun initListener() {
        binding.btnUpdateJournalContentComplete.setOnClickListener {
            viewModel.updateTravelJournalContent()
        }
        binding.tbUpdateJournalContent.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnUpdateJournalContentDate.setOnClickListener {
            viewModel.onPickDailyDate()
        }
        binding.btnUpdateJournalContentsPlace.setOnClickListener {
        }

        binding.btnUpdateJournalContentSelectPicture.setOnClickListener {
            viewModel.onSelectPictures(pickImageUseCase)
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner){
            viewModel.date.collectLatest {
                binding.btnUpdateJournalContentDate.text = it.toDateString()
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.placeName.collectLatest {
                binding.btnUpdateJournalContentsPlace.text = it ?: getString(R.string.post_travel_log_daily_place)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.images.collectLatest {
                Log.d("jomi", "imgaeList : $it")
                pictureAdapter.submitList(it)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvUpdateJournalContentPictures.adapter = null
        super.onDestroyView()
    }

    private fun deleteSelectedPicture(imageIndex: Int) {
        val newImages = pictureAdapter.currentList.toMutableList().apply {
            removeAt(imageIndex)
        }
        viewModel.deletePicture(newImages)
    }

    private fun showDailyDatePickerDialog(
        currentDate: LocalDate?,
        minDateMillis: Long,
        maxDateMillis: Long,
    ) {
        if (dailyDatePickerDialog == null) {
            dailyDatePickerDialog = getDailyDatePickerDialog(currentDate, minDateMillis, maxDateMillis)
        }
        if (dailyDatePickerDialog?.isShowing == false) {
            dailyDatePickerDialog?.show()
        }
    }

    private fun getDailyDatePickerDialog(
        initDate: LocalDate?,
        minDateMillis: Long,
        maxDateMillis: Long,
    ): DatePickerDialog {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.onDailyDateSelected(year, month, dayOfMonth)
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

    private fun LocalDate.toDateString(): String {
        return binding.root.context.getString(
            R.string.date_picker_daily,
            year,
            monthValue,
            dayOfMonth,
            dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
        )
    }

    private fun handleEvent(event: TravelJournalContentUpdateViewModel.Event) {
        when (event) {
            is TravelJournalContentUpdateViewModel.Event.ShowDailyDatePicker -> {
                showDailyDatePickerDialog(
                    event.currentDate,
                    event.minDateMillis,
                    event.maxDateMillis
                )
            }
            TravelJournalContentUpdateViewModel.Event.ClearDatePickerDialog -> {
                dailyDatePickerDialog = null
            }
            TravelJournalContentUpdateViewModel.Event.NoContentData -> {
                sendSnackBar("여행일지 컨텐츠를 작성해 주세요")
            }
            TravelJournalContentUpdateViewModel.Event.NoImagesData -> {
                sendSnackBar("최소 한장 이상의 이미지를 선택해주세요")
            }
        }
    }
}
