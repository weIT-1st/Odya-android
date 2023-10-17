package com.weit.presentation.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMapBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.searchplace.SearchPlaceBottomSheetFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MapFragment :
    BaseFragment<FragmentMapBinding>(
        FragmentMapBinding::inflate,
    ),
    OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()

    private val adapter = PlacePredictionAdapter {
        binding.rvPlacePrediction.visibility = View.GONE
        viewModel.getDetailPlace(it)
    }

    private var searchPlaceBottomSheetFragment: SearchPlaceBottomSheetFragment? = null

    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initRecyclerView()
        initSearchView()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchPlaceList.collectLatest {
                adapter.submitList(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.detailPlace.collectLatest {
                val latlng = LatLng(it.latitude!!, it.longitude!!)
                showMap(latlng)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.touchPlaceId.collectLatest { placeId ->
                if (placeId.isNotBlank()) {
                    placeBottomSheetUp(placeId)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.currentLatLng.collectLatest {
                showMap(it)
            }
        }
    }

    private fun initSearchView() {
        binding.svSearch.run {
            isIconified = false
            queryHint = getString(R.string.search_a_place)
            requestFocusFromTouch()
        }

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                binding.rvPlacePrediction.visibility = View.VISIBLE
                viewModel.searchPlace(newText)
                return true
            }
        })
    }

    private fun initRecyclerView() {
        binding.rvPlacePrediction.adapter = adapter
        binding.rvPlacePrediction.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    private fun showMap(latLng: LatLng) {
        coordinates = latLng

        mapFragment =
            requireActivity().supportFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?

        if (mapFragment == null) {
            val mapOptions = GoogleMapOptions()
            mapOptions.mapToolbarEnabled(false)

            mapFragment = SupportMapFragment.newInstance(mapOptions)

            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.confirmation_map,
                    mapFragment!!,
                    MAP_FRAGMENT_TAG,
                )
                .commit()

            mapFragment!!.getMapAsync(this)
        } else {
            updateMap(coordinates)
        }
    }
    private fun updateMap(latLng: LatLng) {
        marker!!.position = latLng
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        try {
            val success = map!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json),
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        marker = map!!.addMarker(MarkerOptions().position(coordinates))

        if (map != null) {
            map!!.setOnMapClickListener {
                viewModel.getPlaceByCoordinate(it.latitude, it.longitude)
            }

            map!!.setOnPoiClickListener {
                placeBottomSheetUp(it.placeId)
            }

        } else {
            sendSnackBar("지도 정보를 받아오지 못했어요")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map?.clear()
        mapFragment?.onDestroyView()
    }

    private fun placeBottomSheetUp(placeId: String) {
        if (searchPlaceBottomSheetFragment == null){
            searchPlaceBottomSheetFragment = SearchPlaceBottomSheetFragment(placeId) {
                placeBottomSheetReset()
            }
        }

        if (!searchPlaceBottomSheetFragment!!.isAdded) {
            searchPlaceBottomSheetFragment!!.show(childFragmentManager, TAG)
        }
    }

    private fun placeBottomSheetReset(){
        searchPlaceBottomSheetFragment = null
    }
    companion object {
        private val TAG = "MapFragment"
        private const val MAP_FRAGMENT_TAG = "MAP"
    }
}
