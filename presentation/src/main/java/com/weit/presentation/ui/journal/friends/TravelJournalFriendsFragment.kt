package com.weit.presentation.ui.journal.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.presentation.databinding.BottomSheetJournalFriendsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalFriendsFragment(
    private val friends: List<TravelJournalCompanionsInfo>
): BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: TravelJournalFriendsViewModel.TravelJournalFriendsFactory

    private val viewModel : TravelJournalFriendsViewModel by viewModels {
        TravelJournalFriendsViewModel.provideFactory(viewModelFactory, friends)
    }

    private var _binding: BottomSheetJournalFriendsBinding? = null
    private val binding get() = _binding!!

//    private val travelJournalFriendAdapter = TravelJournalFriendAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetJournalFriendsBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.rvTravelJournalFriends.adapter = travelJournalFriendAdapter
//
//        travelJournalFriendAdapter.submitList(friends)
    }

    override fun onDestroyView() {
        binding.rvTravelJournalFriends.adapter = null
        super.onDestroyView()
    }
}
