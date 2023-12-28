package com.weit.presentation.ui.journal.album

import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.databinding.ItemJournalDetailAlbumModelBinding
import com.weit.presentation.ui.base.BaseFragment

class TravelJournalAlbumFragment(
    private val travelJournalInfo: TravelJournalInfo
): BaseFragment<ItemJournalDetailAlbumModelBinding>(
    ItemJournalDetailAlbumModelBinding::inflate
) {
}
