package com.weit.presentation.ui.journal.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.databinding.BottomSheetJournalMyMenuBinding
import dagger.hilt.android.AndroidEntryPoint

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
            dismiss()
        }
        binding.tvTravelJournalDelete.setOnClickListener {
            onClickDelete()
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
