package com.weit.presentation.ui.main.place

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.weit.presentation.ui.main.community.PlaceCommunityFragment
import com.weit.presentation.ui.main.journal.PlaceJournalFragment
import com.weit.presentation.ui.main.review.PlaceReviewFragment

class SearchPlaceTapAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val placeId: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = SEARCH_PLACE_TAP_COUNT
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { PlaceJournalFragment(placeId) }
            1 -> { PlaceReviewFragment(placeId) }
            else -> { PlaceCommunityFragment(placeId) }
        }
    }

    companion object {
        private const val SEARCH_PLACE_TAP_COUNT = 3
    }
}
