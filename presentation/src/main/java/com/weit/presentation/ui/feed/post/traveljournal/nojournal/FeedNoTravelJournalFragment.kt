package com.weit.presentation.ui.feed.post.traveljournal.nojournal

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentFeedPostNoTravellogBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedNoTravelJournalFragment : BaseFragment<FragmentFeedPostNoTravellogBinding>(
    FragmentFeedPostNoTravellogBinding::inflate,
) {

    private var feedNoTravelJournalDialog: FeedNoTravelJournalDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun initListener() {
        super.initListener()
        binding.ivFeedNoTravelLog.setOnClickListener { findNavController().popBackStack() }
        binding.btnFeedNoTravelLogWrite.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.feed_travel_journal_no_title))
                .setMessage(getString(R.string.feed_travel_journal_no_content))
                .setNegativeButton(getString(R.string.feed_travel_journal_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.feed_travel_journal_write)) { dialog, which ->
                    goToTravelJournalWrite()
                }
                .show()
        }
    }

    private fun goToTravelJournalWrite(){
        val action =
            FeedNoTravelJournalFragmentDirections.actionFeedNoTravelJournalFragmentToPostGraph()
        findNavController().navigate(action)
    }

}
