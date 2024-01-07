package com.weit.presentation.ui.feed.post.traveljournal.nojournal

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
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
            if (feedNoTravelJournalDialog == null) {
                feedNoTravelJournalDialog = FeedNoTravelJournalDialogFragment(
                    onComplete = { goToTravelJournalWrite() }
                )
            }
            if (feedNoTravelJournalDialog?.isAdded?.not() == true) {
                feedNoTravelJournalDialog?.show(
                    requireActivity().supportFragmentManager,
                    "feedNoTravelJournalDialog",
                )
            }
        }
    }

    private fun goToTravelJournalWrite(){
        val action =
            FeedNoTravelJournalFragmentDirections.actionFeedNoTravelJournalFragmentToPostGraph()
        findNavController().navigate(action)
    }

}
