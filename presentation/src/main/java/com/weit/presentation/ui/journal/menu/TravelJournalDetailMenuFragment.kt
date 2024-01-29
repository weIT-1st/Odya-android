package com.weit.presentation.ui.journal.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.databinding.BottomSheetJournalMyMenuBinding
import com.weit.presentation.ui.journal.travel_journal.TravelJournalFragmentDirections
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalDetailMenuFragment(
    private val onClickUpdate: () -> Unit,
    private val onClickDelete: () -> Unit,
) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetJournalMyMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetJournalMyMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTravelJournalUpdate.setOnClickListener {
            onClickUpdate()
            dismiss()
        }

        binding.tvTravelJournalShare.setOnClickListener {
            // todo 공유
        }
        binding.tvTravelJournalDelete.setOnClickListener {
            onClickDelete
            dismiss()
        }
        binding.tvTravelJournalClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
