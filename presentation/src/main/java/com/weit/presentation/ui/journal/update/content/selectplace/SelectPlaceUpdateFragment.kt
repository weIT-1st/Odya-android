package com.weit.presentation.ui.journal.update.content.selectplace

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentUpdateSelectPlaceBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.post.selectplace.SelectPlaceAction
import com.weit.presentation.ui.post.selectplace.SelectPlaceAdapter
import com.weit.presentation.ui.util.getMarkerIconFromDrawable
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SelectPlaceUpdateFragment : BaseMapFragment<FragmentUpdateSelectPlaceBinding>(
    FragmentUpdateSelectPlaceBinding::inflate,
    FragmentUpdateSelectPlaceBinding::mapSelectPlace
), OnMapReadyCallback {

    private val args: SelectPlaceUpdateFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: SelectPlaceUpdateViewModel.SelectPlaceUpdateFactory

    private val viewModel: SelectPlaceUpdateViewModel by viewModels {
        SelectPlaceUpdateViewModel.create(viewModelFactory, args.imagePlaces?.toList() ?: emptyList())
    }

    private val sheetBehavior by lazy {
        BottomSheetBehavior.from(binding.bsSelectPlace)
    }

    private val adapter = SelectPlaceAdapter { action ->
        handleAdapterAction(action)
    }

    private val marker by lazy {
        getMarkerIconFromDrawable(resources)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvSelectPlacePredictions.adapter = adapter
        initBottomSheet()
    }

    override fun initListener() {
        binding.tbSelectPlace.setOnMenuItemClickListener { menu ->
            handleMenuItem(menu.itemId)
            true
        }
        binding.tbSelectPlace.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.query.collectLatest {
                viewModel.onSearch(it)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.placeEntities.collectLatest {
                adapter.submitList(it)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.currentAddress.collectLatest {
                binding.tvSelectPlaceCurrentAddress.run {
                    isVisible = it.isNotEmpty()
                    text = it
                }
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleAdapterAction(action: SelectPlaceAction) {
        when (action) {
            is SelectPlaceAction.OnClickPlace -> {
                viewModel.onClickSearchedPlace(action.placePrediction)
            }
        }
    }

    private fun handleMenuItem(itemId: Int) {
        when (itemId) {
            R.id.menu_complete -> {
                viewModel.onComplete()
            }
        }
    }

    private fun initBottomSheet() {
        sheetBehavior.run {
            isHideable = true
            peekHeight = 40
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val seoul = LatLng(37.554891, 126.970814)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(seoul,
                DEFAULT_ZOOM
            ))
        googleMap.setOnPoiClickListener {
            viewModel.onClickPointOfInterest(it)
        }
    }

    private fun handleEvent(event: SelectPlaceUpdateViewModel.Event) {
        when (event) {
            is SelectPlaceUpdateViewModel.Event.SetMarker -> {
                map?.clear()
                val marker = MarkerOptions().apply {
                    position(event.latLng)
                    icon(marker)
                }
                map?.addMarker(marker)
            }
            is SelectPlaceUpdateViewModel.Event.MoveMap -> {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(event.latLng,
                    DEFAULT_ZOOM
                )
                map?.moveCamera(cameraUpdate)
            }
            is SelectPlaceUpdateViewModel.Event.OnComplete -> {
                val action = SelectPlaceUpdateFragmentDirections.actionSelectPlaceUpdateFragmentToTravelJournalContentUpdateFragment(
                    args.travelJournalUpdateContentDTO,
                    event.dto
                )
                findNavController().navigate(action)
            }
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
    }
}
