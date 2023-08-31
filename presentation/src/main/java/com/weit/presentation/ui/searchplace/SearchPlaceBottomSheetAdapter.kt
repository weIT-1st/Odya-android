package com.weit.presentation.ui.searchplace

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.ui.searchplace.community.PlaceCommunityFragment
import com.weit.presentation.ui.searchplace.journey.PlaceJourneyFragment
import com.weit.presentation.ui.searchplace.review.PlaceReviewFragment

class SearchPlaceBottomSheetAdapter(
    fragmentActivity: BottomSheetDialogFragment
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return  ITEM_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PlaceJourneyFragment()
            1 -> PlaceReviewFragment()
            2 -> PlaceCommunityFragment()
            else -> PlaceReviewFragment()
        }
    }

    companion object{
        const val ITEM_COUNT = 3
    }
}
