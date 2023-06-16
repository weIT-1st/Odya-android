package com.weit.presentation.ui.map

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.widget.SearchView
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.GsonBuilder
import com.weit.presentation.BuildConfig
import com.weit.presentation.R
import com.weit.presentation.databinding.FragmentMapBinding
import com.weit.presentation.model.map.GeocodingResult
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(
    FragmentMapBinding::inflate,
) , OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val adapter = PlacePredictionAdapter()
    private val gson = GsonBuilder().registerTypeAdapter(LatLng::class.java, LatLngAdapter()).create()

    private lateinit var queue: RequestQueue
    private lateinit var placesClient: PlacesClient
    private var sessionToken: AutocompleteSessionToken? = null

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initPlaceClient()
        searchView = binding.svSearch
        placesClient = Places.createClient(context)
        queue = Volley.newRequestQueue(context)
        initRecyclerView()
        initSearchView(searchView)
    }
    private fun initPlaceClient(){
        val apiKey = BuildConfig.GOOGLE_MAP_KEY
        if (apiKey.isEmpty()) {
            Toast.makeText(context, getString(R.string.error_api_key), Toast.LENGTH_LONG).show()
            return
        }

        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
    }

    private fun initSearchView(searchView: SearchView) {
        searchView.queryHint = getString(R.string.search_a_place)
        searchView.isIconifiedByDefault = false
        searchView.isFocusable = true
        searchView.isIconified = false
        searchView.requestFocusFromTouch()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                recyclerView.visibility=View.VISIBLE
                handler.removeCallbacksAndMessages(null)
                //위치를 고려해봐야함
                sessionToken = AutocompleteSessionToken.newInstance()

                handler.postDelayed({ getPlacePredictions(newText) }, 300)
                return true
            }
        })
    }

    private fun initRecyclerView() {
                recyclerView = binding.rvPlacePrediction
                val layoutManager = LinearLayoutManager(context)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
                recyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
                adapter.onPlaceClickListener = {
                    recyclerView.visibility=View.GONE
                    geocodePlaceAndDisplay(it) }
    }


    private fun getPlacePredictions(query: String) {
        val bias: LocationBias = RectangularBounds.newInstance(
            LatLng(22.458744, 88.208162),
            LatLng(22.730671, 88.524896)
        )

        val newRequest = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(bias)
            .setCountries("KR")
            .setTypesFilter(listOf(PlaceTypes.ESTABLISHMENT))
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(newRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                adapter.setPredictions(predictions)
            }.addOnFailureListener { exception: Exception? ->

                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.message}")
                }
            }
    }

    private fun geocodePlaceAndDisplay(placePrediction: AutocompletePrediction) {
        val apiKey = BuildConfig.GOOGLE_MAP_KEY
        val requestURL =
            "https://maps.googleapis.com/maps/api/geocode/json?place_id=${placePrediction.placeId}&key=$apiKey"


        val request = JsonObjectRequest(Request.Method.GET, requestURL, null, { response ->
            try {
                val status: String = response.getString("status")
                if (status != "OK") {
                    Log.e(TAG, "$status " + response.getString("error_message"))
                }

                val results: JSONArray = response.getJSONArray("results")
                if (results.length() == 0) {
                    Log.w(TAG, "No results from geocoding request.")
                    return@JsonObjectRequest
                }

                val result: GeocodingResult = gson.fromJson(results.getString(0), GeocodingResult::class.java)
                result.geometry?.location?.let { showMap(it) }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error ->
            Log.e(TAG, "Request failed", error)
        })

        queue.add(request)
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
                    MAP_FRAGMENT_TAG
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
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        marker = coordinates?.let { MarkerOptions().position(it) }?.let { map!!.addMarker(it) }
    }
    companion object {
        private val TAG = "MapFragment"
        private const val MAP_FRAGMENT_TAG = "MAP"
    }


}
