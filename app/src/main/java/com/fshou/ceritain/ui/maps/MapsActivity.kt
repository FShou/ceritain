package com.fshou.ceritain.ui.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fshou.ceritain.R
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.ActivityMapsBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.Builder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener { finishAfterTransition() }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
        setMapStyle()
        viewModel
            .getStoriesWithLocation()
            .observe(this) { handleResult(it) }

    }


    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun handleResult(result: Result<List<Story>>) {
        when (result) {
            is Result.Loading -> {
                showLoadingUi(true)
            }

            is Result.Error -> {
                showLoadingUi(false)
                Toast.makeText(
                    this@MapsActivity,
                    result.error,
                    Toast.LENGTH_LONG
                ).show()
            }

            is Result.Success -> {
                showLoadingUi(false)
                addStoriesMarker(result.data)
                boundMarkers()
            }
        }
    }

    private fun showLoadingUi(isLoading: Boolean) {
        if (isLoading) with(binding) {
            map.alpha = 0.5f
            progressBar.visibility = View.VISIBLE
        } else with(binding) {
            map.alpha = 1f
            progressBar.visibility = View.GONE
        }
    }


    private fun boundMarkers() {
        val bounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun addStoriesMarker(stories: List<Story>) {
        stories.forEach { story ->
            println(story)
            val latLng = LatLng(story.lat as Double, story.lon as Double)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            )
            boundsBuilder.include(latLng)
        }

    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}