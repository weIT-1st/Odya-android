package com.weit.presentation.ui.journal.travel_journal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelJournalBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.journal.menu.TravelJournalDetailMenuFragment
import com.weit.presentation.ui.util.SpaceDecoration
import com.weit.presentation.ui.util.getMarkerIconFromDrawable
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalFragment : BaseMapFragment<FragmentTravelJournalBinding>(
    FragmentTravelJournalBinding::inflate,
    FragmentTravelJournalBinding::mapTravelJournal
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

    private val marker by lazy {
        getMarkerIconFromDrawable(resources)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initFriendRV()
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
                    initTravelJournalMap(info)
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
            binding.tbTravelJournal.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_journal_bookmark -> {
                        // todo bookmark 처리
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

    private fun initFriendRV() {
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
    }

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
            adapter = TravelJournalModelAdapter(childFragmentManager, lifecycle, info)
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

    private fun initTravelJournalMap(info: TravelJournalInfo) {
        val latitudes = emptyList<Double>()
        val longitudes = emptyList<Double>()
        info.travelJournalContents.forEach {
            val lat = it.placeDetail.latitude
            val lng = it.placeDetail.longitude

            if (lat == null) {
                return@forEach
            }

            if (lng == null) {
                return@forEach
            }

            latitudes.plus(lat)
            longitudes.plus(lng)
            val marker = MarkerOptions().apply {
                position(LatLng(lat, lng))
                icon(marker)
            }
            map?.addMarker(marker)
        }

        moveToTravelCenter(latitudes, longitudes)
    }

    private fun popUpJournalMenuBottomSheet() {
        if (travelJournalDetailMenuFragment == null) {
            travelJournalDetailMenuFragment = TravelJournalDetailMenuFragment(
                { viewModel.moveToJournalUpdate() },
                { viewModel.deleteTravelJournal() }
            )
        }

        if (!travelJournalDetailMenuFragment!!.isAdded) {
            travelJournalDetailMenuFragment!!.show(childFragmentManager, TAG)
        }
    }

    private fun moveToTravelCenter(latitudes: List<Double>, longitudes: List<Double>) {
        val centerLat = latitudes.average()
        val centerLng = longitudes.average()

        if (centerLng.isNaN() || centerLat.isNaN()) {
            // 비워둠
        } else {
            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(centerLat, centerLng)))
        }

    }

    private fun handleEvent(event: TravelJournalViewModel.Event) {
        when (event) {
            is TravelJournalViewModel.Event.MoveToJournalUpdate -> {
//                travelJournalDetailFragment = null
//
//                val action = TravelJournalFragmentDirections.actionFragmentTravelJournalToTravelJournalUpdateFragment(event.travelJournalUpdateDTO)
//                findNavController().navigate(action)

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

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val seoul = LatLng(37.554891, 126.970814)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                seoul, DEFAULT_ZOOM
            )
        )
    }

    companion object {
        private const val TAG = "TravelJournalFragment"
        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
        private const val DEFAULT_ZOOM = 15f
    }
}
