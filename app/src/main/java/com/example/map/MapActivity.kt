package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation: Location
    private lateinit var draggableMarker: MarkerOptions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker)
       val mapView = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapView.getMapAsync(this)
        startLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val initialPosition = LatLng(40.7128, -74.0060)
        draggableMarker = MarkerOptions().position(initialPosition).draggable(true)
        googleMap.addMarker(draggableMarker)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))

        // Set up marker drag listener
        googleMap.setOnMarkerDragListener(this)
    }
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 0
            fastestInterval = 0
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement=20f
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.requestLocationUpdates(locationRequest, object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if(isLocationInDesiredCountry(location)) {
                        mLastLocation = location
                        updateMapWithCurrentLocation()
                    }
                }

            }
        }, null)

    }
    private fun isLocationInDesiredCountry(location: Location): Boolean {
        val desiredCountryBounds = LatLngBounds(
            LatLng(8.4, 68.1), // Southwest corner of the desired country
            LatLng(37.6, 97.4)  // Northeast corner of the desired country
        )

        // Check if the location falls within the desired country bounds
        val locationLatLng = LatLng(location.latitude, location.longitude)
        return desiredCountryBounds.contains(locationLatLng)
    }
    private fun updateMapWithCurrentLocation() {
        val currentLatLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    override fun onMarkerDrag(marker: Marker) {
        val newPosition = marker?.position
        newPosition?.let {
            var textView:TextView=findViewById(R.id.position_textview)
            textView.text = "Current Position: $newPosition"
        }
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val newPosition = marker?.position
        newPosition?.let {
            Toast.makeText(this, "Marker moved to: $it", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMarkerDragStart(marker: Marker) {
        Toast.makeText(this, "Marker drag started", Toast.LENGTH_SHORT).show()
    }

}