package com.weit.presentation.ui.journal.update.content.selectplace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentUpdateSelectPlaceBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.post.selectplace.SelectPlaceAction
import com.weit.presentation.ui.post.selectplace.SelectPlaceAdapter
import com.weit.presentation.ui.post.selectplace.SelectPlaceFragment
import com.weit.presentation.ui.util.getMarkerIconFromDrawable
import dagger.hilt.android.AndroidEntryPoint
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
        SelectPlaceUpdateViewModel.create(viewModelFactory, args.imagePlaces.toList())
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
        super.initCollector()
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
                // todo
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

    companion object {
        private const val DEFAULT_ZOOM = 15f
    }
}
