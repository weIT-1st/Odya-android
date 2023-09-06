package com.weit.presentation.ui.searchplace

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.ui.searchplace.community.PlaceCommunityFragment
import com.weit.presentation.ui.searchplace.journey.PlaceJourneyFragment
import com.weit.presentation.ui.searchplace.review.PlaceReviewFragment

class SearchPlaceBottomSheetAdapter(
    fragment: BottomSheetDialogFragment,
    private val tabItem: ArrayList<Fragment>
): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabItem.size
    override fun createFragment(position: Int): Fragment = tabItem[position]
}
