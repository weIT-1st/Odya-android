package com.weit.presentation.ui.journal.travel_journal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.model.journal.TravelJournalContentUpdateDTO
import com.weit.presentation.ui.journal.album.TravelJournalAlbumFragment
import com.weit.presentation.ui.journal.basic.TravelJournalBasicFragment

class TravelJournalModelAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val travelJournalInfo: TravelJournalInfo,
    private val moveToTravelJournalContentsUpdate: (TravelJournalContentUpdateDTO) -> Unit
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val travelJournalBasicFragment = TravelJournalBasicFragment(travelJournalInfo) {
        moveToTravelJournalContentsUpdate(it)
    }
    private val travelJournalAlbumFragment = TravelJournalAlbumFragment(travelJournalInfo) {
        moveToTravelJournalContentsUpdate(it)
    }

    override fun getItemCount(): Int = TRAVEL_JOURNAL_MODEL_COUNT
    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> { travelJournalBasicFragment }
            1 -> { travelJournalAlbumFragment }
            else -> { travelJournalBasicFragment }
        }


    companion object {
        private const val TRAVEL_JOURNAL_MODEL_COUNT = 2
    }
}
