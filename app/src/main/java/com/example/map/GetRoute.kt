package com.example.map

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request

class GetRoute : AppCompatActivity() , OnMapReadyCallback
{

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var map: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_direction)
     //   val mapFragment =
           // supportFragmentManager.findFragmentById(R.id.directionMap) as SupportMapFragment
     //   mapFragment.getMapAsync(this)
     //   fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map=googleMap
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
        map.isMyLocationEnabled=true
        map.uiSettings.isZoomControlsEnabled=true
        fetchMyLocation()


    }

    private fun fetchMyLocation() {
        var task: Unit = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            Toast.makeText(this,"Sorry the permission denied",Toast.LENGTH_SHORT).show()
        }
        fusedLocationProviderClient.lastLocation


    }
}


