package com.weit.presentation.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentExampleBinding
import com.weit.presentation.databinding.FragmentMapBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(
    FragmentMapBinding::inflate,
), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap : GoogleMap) {
        this.googleMap = googleMap

        currentMarker = setupMarker(LatLngEntity(37.5562,126.9724))  // default 서울역
        currentMarker?.showInfoWindow()
    }

    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {

        // 1. icongenerator로 marker만들기
        var iconGenerator = IconGenerator(context)
        var markerView: View = LayoutInflater.from(context).inflate(R.layout.layout_marker, null)
//        var marker = markerView.findViewById<CircleImageView>(R.id.circle_iv)
//        Glide.with(markerView)
//            .load()
//            .into(marker)

        iconGenerator.setContentView(markerView)
        iconGenerator.setBackground(null)

        var icon = iconGenerator.makeIcon()
        var bitmap = icon


        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)

        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
//            title("위치")
//            snippet("서울역 위치")
            icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15f))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))

        return googleMap.addMarker(markerOption)

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )

}
