package com.weit.presentation.ui.map

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.weit.domain.model.image.CoordinateUserImageResponseInfo
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMapBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.login.input.topic.LoginTopicFragment
import com.weit.presentation.ui.map.search.MainSearchTopSheetFragment
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

    private var searchPlaceBottomSheetFragment: SearchPlaceBottomSheetFragment? = null
    private var mainSearchTopSheetFragment: MainSearchTopSheetFragment? = null

    private var mapFragment: SupportMapFragment? = null
    private var coordinates : LatLng = DEFAULT_LAT_LNG
    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.btnMapPopularOdya.setOnClickListener {
            showMainSearchTopSheet()
        }

        binding.btnMapCurrentLocate.setOnClickListener {
            updateMap(coordinates)
        }

        binding.toggleMapLoogOdya.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeOdyaToggleOff(isChecked)
        }
    }

    override fun initCollector() {
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

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.currentLatLng.collectLatest {
                showMap(it)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.odyaList.collectLatest {
                for (item in it) {
                    addMarker(item)
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest {event ->
                handleEvent(event)
            }
        }
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
        marker.let {
            marker?.position = latLng
        }
        map.let{
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
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

        map?.setOnCameraIdleListener {
            viewModel.getOdyaList(
                map!!.projection.visibleRegion.latLngBounds.northeast,
                map!!.projection.visibleRegion.latLngBounds.southwest
            )
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

    private fun showMainSearchTopSheet(){
        if (mainSearchTopSheetFragment == null){
            mainSearchTopSheetFragment = MainSearchTopSheetFragment{
                viewModel.getDetailPlace(it)
            }
        }
        if (!mainSearchTopSheetFragment!!.isAdded) {
            mainSearchTopSheetFragment!!.show(childFragmentManager, TAG)
        }
    }


    private fun addMarker(place: CoordinateUserImageResponseInfo): Marker {
        val odyaMarkerPin =
            LayoutInflater.from(requireContext()).inflate(R.layout.item_odya_pin, null, false)
        val ivOdyaPin = odyaMarkerPin.findViewById<ImageView>(R.id.iv_item_odya_pin)
        val position = LatLng(place.latitude, place.longitude)

        Glide.with(requireContext())
            .load(place.imageUrl)
            .placeholder(R.layout.image_placeholder.toDrawable())
            .into(ivOdyaPin)

        val markerOption = MarkerOptions()
        getBitmapFromView(odyaMarkerPin){
            if (it == null){
                val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_odya_pin, null)
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap

                markerOption
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            } else {
                markerOption
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromBitmap(it!!))
            }
        }

        return map!!.addMarker(markerOption)!!
    }

    private fun getBitmapFromView(view: View, callback: (Bitmap?) -> Unit) {
        requireActivity().window?.let{window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

            val locationOfViewWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewWindow)

            try {
                PixelCopy.request(
                    window,
                    Rect(locationOfViewWindow[0],
                        locationOfViewWindow[1],
                        locationOfViewWindow[0] + view.width,
                        locationOfViewWindow[1] + view.height),
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) callback.invoke(bitmap)
                        else callback.invoke(null)
                    },
                    Handler(Looper.getMainLooper())
                )
            } catch (e: IllegalAccessException){
                callback.invoke(null)
            }
        }
    }


    private fun handleEvent(event : MapViewModel.Event) {
        when (event) {
            MapViewModel.Event.FirstLogin -> {
                val action = MapFragmentDirections.actionFragmentMapToLoginTopicFragment()
                findNavController().navigate(action)
            }
        }
    }

    companion object {
        private const val TAG = "MapFragment"
        private const val MAP_FRAGMENT_TAG = "MAP"
        private val DEFAULT_LAT_LNG = LatLng(37.55476719052827, 126.97082417355988)
    }
}
