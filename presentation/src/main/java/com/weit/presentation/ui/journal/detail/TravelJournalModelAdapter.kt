package com.weit.presentation.ui.journal.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.ui.journal.album.TravelJournalAlbumFragment
import com.weit.presentation.ui.journal.basic.TravelJournalBasicFragment

class TravelJournalModelAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val travelJournalInfo: TravelJournalInfo
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = TRAVEL_JOURNAL_MODEL_COUNT

    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> { TravelJournalBasicFragment(travelJournalInfo) }
            1 -> { TravelJournalAlbumFragment(travelJournalInfo) }
            else -> { TravelJournalBasicFragment(travelJournalInfo) }
        }

    companion object {
        private const val TRAVEL_JOURNAL_MODEL_COUNT = 2
    }
}
