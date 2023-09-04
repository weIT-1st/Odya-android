package com.weit.presentation.ui.post.selectplace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.weit.presentation.databinding.FragmentSelectPlaceBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SelectPlaceFragment :
    BaseMapFragment<FragmentSelectPlaceBinding>(
        FragmentSelectPlaceBinding::inflate,
        FragmentSelectPlaceBinding::mapSelectPlace,
    ),
    OnMapReadyCallback {

    private val args: SelectPlaceFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: SelectPlaceViewModel.SelectPlaceFactory

    private val viewModel: SelectPlaceViewModel by viewModels {
        SelectPlaceViewModel.create(viewModelFactory, args.imagePlaces.toList())
    }

    private val sheetBehavior by lazy {
        BottomSheetBehavior.from(binding.bsSelectPlace)
    }

    private val adapter = SelectPlaceAdapter { action ->
        handleAdapterAction(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.rvSelectPlacePredictions.adapter = adapter
        initBottomSheet()
    }

    private fun initBottomSheet() {
        sheetBehavior.run {
            isHideable = true
            peekHeight = 50
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collect { event ->
                handleEvent(event)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.query.collectLatest { query ->
                viewModel.onSearch(query)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.places.collectLatest { places ->
                adapter.submitList(places)
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

    private fun handleEvent(event: SelectPlaceViewModel.Event) {
        when (event) {
            is SelectPlaceViewModel.Event.SetMarker -> {
                map?.clear()
                val marker = MarkerOptions().apply {
                    position(event.latLng)
                }
                map?.addMarker(marker)
            }
            is SelectPlaceViewModel.Event.MoveMap -> {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(event.latLng, 15f)
                map?.moveCamera(cameraUpdate)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val seoul = LatLng(37.554891, 126.970814)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))
        googleMap.setOnPoiClickListener {
            viewModel.onClickPointOfInterest(it)
        }
    }

    override fun onDestroyView() {
        binding.rvSelectPlacePredictions.adapter = null
        super.onDestroyView()
    }
}
