package com.weit.presentation.ui.journal.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.weit.domain.model.journal.TravelJournalInfo
import com.weit.domain.model.place.PlaceDetail
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentTravelJournalMapBinding
import com.weit.presentation.ui.base.BaseMapFragment
import com.weit.presentation.ui.util.getMarkerIconFromDrawable
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TravelJournalMapFragment(
    private val travelJournalInfo: TravelJournalInfo,
) : BaseMapFragment<FragmentTravelJournalMapBinding>(
    FragmentTravelJournalMapBinding::inflate,
    FragmentTravelJournalMapBinding::mapTravelJournalMap
),
    OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: TravelJournalMapViewModel.TravelJournalInfoFactory

    private val viewModel: TravelJournalMapViewModel by viewModels {
        TravelJournalMapViewModel.provideFactory(viewModelFactory, travelJournalInfo)
    }

    private val marker by lazy {
        getMarkerIconFromDrawable(resources)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel
        setByMapMode(travelJournalInfo)
    }

    override fun initCollector() {
        super.initCollector()

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.mainPlaceList.collectLatest { placePinInfo ->
                setMapPin(placePinInfo)
                moveToTravelCenter(
                    placePinInfo.map { it.latitude },
                    placePinInfo.map { it.longitude })
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.allPlaceList.collectLatest {
                setMapLine(it)
            }
        }
    }

    private fun setByMapMode(info: TravelJournalInfo) {
        binding.tvTravelJournalMapTitle.text = info.travelJournalTitle
        binding.tvTravelJournalMapDate.text = requireContext().getString(
            R.string.journal_memory_my_travel_date,
            info.travelStartDate,
            info.travelEndDate
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        val seoul = LatLng(37.554891, 126.970814)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                seoul, DEFAULT_ZOOM
            )
        )

        viewModel.initMainPlaceList()
        viewModel.initAllPlaceList()
    }

    private fun setMapPin(placeList: List<PlaceDetail>) {
        placeList.forEach { info ->
            if (info.latitude == null) {
                return@forEach
            }
            if (info.longitude == null) {
                return@forEach
            }

            val markerOption = MarkerOptions().apply {
                position(LatLng(info.latitude!!, info.longitude!!))
                icon(marker)
            }
            map?.addMarker(markerOption)


        }
    }

    private fun setMapLine(placeList: List<LatLng>) {
        val polyLineOption = PolylineOptions().apply {
            placeList.forEach {
                add(it)
            }
            color(R.color.primary)
            jointType(JointType.ROUND)
        }

        map?.addPolyline(polyLineOption)
    }

    private fun moveToTravelCenter(latitudes: List<Double?>, longitudes: List<Double?>) {
        val centerLat = latitudes.filterNotNull().average()
        val centerLng = longitudes.filterNotNull().average()

        if (centerLng.isNaN() || centerLat.isNaN()) {
            // 비워둠
        } else {
            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(centerLat, centerLng)))
        }
    }

    companion object {
        private const val MAX_ABLE_SHOW_FRIENDS_NUM = 3
        private const val DEFAULT_ZOOM = 15f
    }
}
