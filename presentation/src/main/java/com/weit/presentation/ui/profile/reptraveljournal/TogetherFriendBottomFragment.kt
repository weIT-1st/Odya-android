package com.weit.presentation.ui.profile.reptraveljournal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.domain.model.journal.TravelJournalCompanionsInfo
import com.weit.presentation.databinding.BottomSheetJournalFriendsBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TogetherFriendBottomFragment(
    private val friends: List<TravelJournalCompanionsInfo>
): BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: TogetherFriendBottomViewModel.TravelJournalFriendsFactory

    private val viewModel : TogetherFriendBottomViewModel by viewModels {
        TogetherFriendBottomViewModel.provideFactory(viewModelFactory, friends)
    }

    private var _binding: BottomSheetJournalFriendsBinding? = null
    private val binding get() = _binding!!

    private val travelJournalFriendAdapter = TogetherFriendAdapter(
        selectFriend = { friend -> viewModel.onFollowStateChange(friend)}
    )

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

        binding.rvTravelJournalFriends.adapter = travelJournalFriendAdapter
        initCollector()

    }

    fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.journalFriends.collectLatest { journalFriends ->
                travelJournalFriendAdapter.submitList(journalFriends)
            }
        }
    }


    override fun onDestroyView() {
        binding.rvTravelJournalFriends.adapter = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "TogetherFriendBottomFragment"
    }
}
