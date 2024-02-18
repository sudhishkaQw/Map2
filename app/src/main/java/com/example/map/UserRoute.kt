package com.example.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONException
import org.json.JSONObject

class UserRoute : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation:Location
    private lateinit var etDestination: EditText
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_route)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        etDestination = findViewById(R.id.etDestination)

        findViewById<Button>(R.id.btnCalculateRoute).setOnClickListener {
            calculateRoute()
        }
       startLocationUpdates()
        var zoomIn:ImageButton=findViewById(R.id.zoomInButtonRoute)
        zoomIn.setOnClickListener {view->
            onZoomInClicked(view)
        }
        var zoomOut:ImageButton=findViewById(R.id.zoomOutButtonRoute)
        zoomOut.setOnClickListener { view->
            onZoomOutClicked(view)
        }

    }

    private fun onZoomInClicked(view: View) {
        mMap?.animateCamera(CameraUpdateFactory.zoomIn())
    }
    private fun onZoomOutClicked(view: View)
    {
        mMap?.animateCamera(CameraUpdateFactory.zoomOut())
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
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                mLastLocation = location
                updateMapWithCurrentLocation()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
    private fun updateMapWithCurrentLocation() {
        val currentLatLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        mMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }
    private fun calculateRoute() {
        val destination = etDestination.text.toString().trim()
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter a destination", Toast.LENGTH_SHORT).show()
            return
        }
        val geocoder = Geocoder(this)
        var destinationLatLng: LatLng? = null
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(destination, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    destinationLatLng = LatLng(address.latitude, address.longitude)
                } else {
                    Toast.makeText(this, "Destination not found", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error converting destination to coordinates: ${e.message}")
            Toast.makeText(this, "Error converting destination to coordinates", Toast.LENGTH_SHORT)
                .show()
            return
        }
        mMap.clear()
        updateMapWithCurrentLocation()
        mMap.addMarker(MarkerOptions().position(destinationLatLng!!).title("Destination"))

        val origin = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        drawRoute(origin, destinationLatLng)
    }
        private fun drawRoute(origin: LatLng, destination: LatLng) {
            // Replace this with your actual Google Maps API key
            val apiKey = "AIzaSyDdyinc_88KQdK0QufHq2qyc6hDkbmgV0s"
            val queue = Volley.newRequestQueue(this)
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origin.latitude},${origin.longitude}&" +
                    "destination=${destination.latitude},${destination.longitude}&" +
                    "key=$apiKey"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response->
                    try {
                        val jsonResponse = JSONObject(response)
                        val routes = jsonResponse.getJSONArray("routes")

                        if (routes.length() > 0) {
                            val legs = routes.getJSONObject(0).getJSONArray("legs")
                            if (legs.length() > 0) {
                                val steps = legs.getJSONObject(0).getJSONArray("steps")
                                val path: MutableList<List<LatLng>> = ArrayList()

                                for (i in 0 until steps.length()) {
                                    val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                                    path.add(PolyUtil.decode(points))
                                }

                                for (i in path.indices) {
                                    mMap.addPolyline(
                                        PolylineOptions()
                                            .addAll(path[i])
                                            .color(Color.BLUE)
                                    )
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Toast.makeText(this, "Error fetching directions: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Directions Error", "Error fetching directions: ${error.message}")
                }
            )
            queue.add(stringRequest)
        }

}