package com.weit.presentation.ui.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.presentation.databinding.FragmentMapBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.map.search.MainSearchTopSheetFragment
import com.weit.presentation.ui.util.getCurrentMarkerIconFromDrawable
import com.weit.presentation.ui.util.getImageMarkerIconFromLayout
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MapFragment :
    BaseMapFragment<FragmentMapBinding>(
        FragmentMapBinding::inflate,
        FragmentMapBinding::mapMainMap
    ),
    OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()

    private var mainSearchTopSheetFragment : MainSearchTopSheetFragment? = null
    private val marker by lazy {
        getCurrentMarkerIconFromDrawable(resources)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.btnMapCurrentLocate.setOnClickListener {
            viewModel.getCurrentPlace()
        }
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.detailPlace.collectLatest {
                moveToMap(it.latitude, it.longitude)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.touchPlaceId.collectLatest { placeId ->
                viewModel.popUpSearchPlace(placeId)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.currentLatLng.collectLatest {
                moveToMap(it.latitude, it.longitude)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.odyaList.collectLatest {
                map?.clear()
                setMapImagePin(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val seoul = LatLng(37.554891, 126.970814)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(seoul, DEFAULT_ZOOM)
        )

        map?.setOnMapClickListener {
            viewModel.getPlaceByCoordinate(it.latitude, it.longitude)
        }

        map?.setOnPoiClickListener{
            viewModel.popUpSearchPlace(it.placeId)
            moveToMap(it.latLng.latitude, it.latLng.longitude)
        }

        map?.setOnCameraIdleListener {
            viewModel.getOdyaList(
                map!!.projection.visibleRegion.latLngBounds.northeast,
                map!!.projection.visibleRegion.latLngBounds.southwest
            )
        }

        map?.setOnMapClickListener {
            viewModel.clickMarker(it)
        }
    }

    private fun setMapImagePin(placeList: List<CoordinateUserImageResponseInfo>) {
        placeList.forEach { info ->
            val imageMarker = getImageMarkerIconFromLayout(requireContext(), info.imageUrl)
            val markerOption = MarkerOptions().apply {
                position(LatLng(info.latitude, info.longitude))
                icon(imageMarker)
            }
            map?.addMarker(markerOption)
        }
    }

    private fun showMainSearchTopSheet() {
        mainSearchTopSheetFragment = null
        mainSearchTopSheetFragment = MainSearchTopSheetFragment { placeId ->
            viewModel.getDetailPlace(placeId)
            viewModel.popUpSearchPlace(placeId)
        }
        if (!mainSearchTopSheetFragment!!.isAdded) {
            mainSearchTopSheetFragment!!.show(childFragmentManager, TAG)
        }
    }

    private fun moveToMap(latitude: Double?, longitude: Double?) {
        if (latitude == null) {
            return
        }

        if (longitude == null) {
            return
        }
        map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
    }

    private fun handleEvent(event: MapViewModel.Event) {
        when (event) {
            MapViewModel.Event.FirstLogin -> {
                val action = MapFragmentDirections.actionFragmentMapToLoginTopicFragment()
                findNavController().navigate(action)
            }

            is MapViewModel.Event.PopUpSearchPlace -> {
                if (event.placeId.isNullOrBlank().not()){
                    val action = MapFragmentDirections.actionFragmentMapToSearchPlaceBottomSheetFragment(
                        event.placeId!!
                    )
                    findNavController().navigate(action)
                }
            }
            is MapViewModel.Event.MoveToTravelJournal -> {
                val action = MapFragmentDirections.actionFragmentMapToFragmentTravelJournal(
                    event.travelJournalId
                )
                findNavController().navigate(action)
            }
            MapViewModel.Event.PopUpMainSearch -> {
                showMainSearchTopSheet()
            }
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val TAG = "MapFragment"
        private const val MAP_FRAGMENT_TAG = "MAP"
    }
}
