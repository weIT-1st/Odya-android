package com.weit.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import kotlin.reflect.KProperty1

abstract class BaseMapFragment<VB : ViewDataBinding>(
    bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    private val mapViewProperty: KProperty1<VB, MapView>,
) : BaseFragment<VB>(bindingFactory), OnMapReadyCallback {

    protected var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMapView().run {
            onCreate(savedInstanceState)
            getMapAsync(this@BaseMapFragment)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun onStart() {
        super.onStart()
        getMapView().onStart()
    }

    override fun onResume() {
        super.onResume()
        getMapView().onResume()
    }

    override fun onPause() {
        super.onPause()
        getMapView().onPause()
    }

    override fun onStop() {
        super.onStop()
        getMapView().onStop()
    }

    override fun onDestroyView() {
        clearMap()
        super.onDestroyView()
    }

    private fun getMapView() = mapViewProperty.get(binding)

    private fun clearMap() {
        getMapView().onDestroy()
        map?.clear()
        map = null
    }
}
