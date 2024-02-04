package com.weit.presentation.ui.journal.travel_journal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelJournalBinding
import com.weit.presentation.model.journal.TravelJournalContentUpdateDTO
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.journal.menu.TravelJournalDetailMenuFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalFragment : BaseFragment<FragmentTravelJournalBinding>(
    FragmentTravelJournalBinding::inflate
) {
    @Inject
    lateinit var viewModelFactory: TravelJournalViewModel.TravelJournalIdFactory

    private val args: TravelJournalFragmentArgs by navArgs()
    private val viewModel: TravelJournalViewModel by viewModels {
        TravelJournalViewModel.provideFactory(viewModelFactory, args.travelJournalId)
    }

    private var travelJournalDetailMenuFragment: TravelJournalDetailMenuFragment? = null

//    private val travelJournalFriendAdapter = TravelJournalFriendAdapter()

    private val sheetBehavior by lazy {
        BottomSheetBehavior.from(binding.bsTravelJournalDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

//        initFriendRV()
        initBottomSheet()
    }

    override fun initListener() {
        binding.tbTravelJournal.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnJournalDetailMoreFriends.setOnClickListener {
            viewModel.popUpJournalFriends()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.journalInfo.collectLatest { info ->
                if (info != null) {
                    binding.tbTravelJournal.title = info.writer.nickname

                    binding.tvJournalDetailMainTitle.text = info.travelJournalTitle
                    binding.tvJournalDetailTravelDate.text =
                        requireContext().getString(
                            R.string.journal_memory_my_travel_date,
                            info.travelStartDate,
                            info.travelEndDate
                        )

                    binding.btnJournalDetailMoreFriends.isGone =
                        info.travelJournalCompanions.size < MAX_ABLE_SHOW_FRIENDS_NUM

                    initJournalModelViewPager(info)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.travelJournalDetailToolBarInfo.collectLatest { info ->
                initMenu(info)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvJournalDetailFriends.adapter = null
        super.onDestroyView()
    }

    private fun initMenu(info: TravelJournalViewModel.TravelJournalDetailToolBarInfo) {
        if (info.isMyTravelJournal) {
            binding.tbTravelJournal.title = info.title
            binding.tbTravelJournal
            binding.tbTravelJournal.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_journal_bookmark -> {
                        // todo 즐겨찾기
                    }

                    R.id.menu_journal_alert -> {
                        viewModel.popUpJournalMenu()
                    }
                }
                true
            }
        } else {
            binding.tbTravelJournal.title = info.title
            binding.tbTravelJournal.menu.clear()
        }
    }

//    private fun initFriendRV() {
//        binding.rvJournalDetailFriends.apply {
//            addItemDecoration(
//                SpaceDecoration(
//                    resources,
//                    rightDP = R.dimen.item_journal_friends_space
//                )
//            )
//            adapter = travelJournalFriendAdapter
//        }
//        travelJournalFriendAdapter.submitList(viewModel.handleFriendsCount())
//    }

    private fun initBottomSheet() {
        sheetBehavior.run {
            peekHeight = 420
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initJournalModelViewPager(info: TravelJournalInfo) {
        val viewPager = binding.viewPagerJournalDetail
        val tabLayout = binding.tlJournalDetailChooseModel

        viewPager.apply {
            adapter = TravelJournalModelAdapter(childFragmentManager, lifecycle, info) {
                moveToTravelJournalContentsUpdate(
                    it
                )
            }
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = requireContext().getDrawable(R.drawable.ic_journal_basic)
                }

                else -> {
                    tab.icon = requireContext().getDrawable(R.drawable.ic_bento_menu)
                }
            }
        }.attach()
    }

    private fun popUpJournalMenuBottomSheet() {
        if (travelJournalDetailMenuFragment == null) {
            travelJournalDetailMenuFragment = TravelJournalDetailMenuFragment(
                { viewModel.moveToJournalUpdate() },
                { onClickDeleteJournal() }
            )
        }

        if (!travelJournalDetailMenuFragment!!.isAdded) {
            travelJournalDetailMenuFragment!!.show(childFragmentManager, TAG)
        }
    }

    private fun onClickDeleteJournal() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.journal_delete_title))
            .setMessage(getString(R.string.journal_delete_message))
            .setNegativeButton(getString(R.string.journal_delete_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.journal_delete_positive)) { _, _ ->
                viewModel.deleteTravelJournal()
            }
            .show()
    }

    private fun moveToTravelJournalContentsUpdate(travelJournalContentUpdateDTO: TravelJournalContentUpdateDTO) {
        val action = TravelJournalFragmentDirections.actionFragmentTravelJournalToTravelJournalContentUpdateFragment(
            travelJournalContentUpdateDTO
        )
        findNavController().navigate(action)
    }

    private fun handleEvent(event: TravelJournalViewModel.Event) {
        when (event) {
            is TravelJournalViewModel.Event.MoveToJournalUpdate -> {
                val action = TravelJournalFragmentDirections.actionFragmentTravelJournalToTravelJournalUpdateFragment(
                    event.travelFriendsDTO.toTypedArray(),
                    event.travelJournalUpdateDTO)
                findNavController().navigate(action)
            }

            is TravelJournalViewModel.Event.PopupTravelJournalMenu -> {
                popUpJournalMenuBottomSheet()
            }

            TravelJournalViewModel.Event.DeleteTravelJournalSuccess -> {
                sendSnackBar("여행일지가 삭제되었습니다.")
            }

            TravelJournalViewModel.Event.PopupTravelJournalFriends -> {

            }
        }
    }

    companion object {
        private const val TAG = "TravelJournalFragment"
        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
    }
}
