package com.weit.presentation.ui.post.selectplace

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
import com.weit.presentation.databinding.FragmentSelectPlaceBinding
import com.weit.presentation.model.post.place.SelectPlaceDTO
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.util.getMarkerIconFromDrawable
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
        SelectPlaceViewModel.create(viewModelFactory, args.imagePlaces.toList(), args.dailyTravelLogPosition)
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

    private fun handleMenuItem(itemId: Int) {
        when (itemId) {
            R.id.menu_complete -> {
                viewModel.onComplete()
                // TODO 장소 들고 넘어가기
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
            viewModel.placeEntities.collectLatest { places ->
                adapter.submitList(places)
            }
        }
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.currentAddress.collectLatest { address ->
                binding.tvSelectPlaceCurrentAddress.run {
                    isVisible = address.isNotEmpty()
                    text = address
                }
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
                    icon(marker)
                }
                map?.addMarker(marker)
            }
            is SelectPlaceViewModel.Event.MoveMap -> {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(event.latLng, DEFAULT_ZOOM)
                map?.moveCamera(cameraUpdate)
            }
            is SelectPlaceViewModel.Event.OnComplete -> {
                moveToPervPage(event.dto)
            }
        }
    }

    private fun moveToPervPage(dto: SelectPlaceDTO) {
        val backStackId = findNavController().previousBackStackEntry?.destination?.id ?: return
        when (backStackId) {
            R.id.postTravelLogFragment -> {
                val direction = SelectPlaceFragmentDirections.actionSelectPlaceFragmentToPostTravelLogFragment(
                    selectPlace = dto,
                )
                findNavController().navigate(direction)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val seoul = LatLng(37.554891, 126.970814)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, DEFAULT_ZOOM))
        googleMap.setOnPoiClickListener {
            viewModel.onClickPointOfInterest(it)
        }
    }

    override fun onDestroyView() {
        binding.rvSelectPlacePredictions.adapter = null
        super.onDestroyView()
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
    }
}
