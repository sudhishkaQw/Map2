package com.example.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MainActivity : AppCompatActivity() , OnMapReadyCallback {
    private var mGoogleMap:GoogleMap? = null
    private lateinit var autoCompleteFragment:AutocompleteSupportFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Places.initialize(applicationContext,getString(R.string.googleMapApiKey))
        autoCompleteFragment = supportFragmentManager.findFragmentById(R.id.autoCompleteFragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.ADDRESS,Place.Field.LAT_LNG))
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener
        {
            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity,"Some Error in Search",Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
              val add = place.address
                 val id = place.id
                val latLng = place.latLng!!
                val marker=addMarker(latLng)
                marker.title="$add"
                marker.snippet="$id"
                zoomOnMap(latLng)
            }

        })

        val mapFragment=supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val mapOptionButton : ImageButton=findViewById(R.id.mapOptionMenu)
        val popupMenu=PopupMenu(this,mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_option,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem->
            changeMap(menuItem.itemId)
            true 
        }
        mapOptionButton.setOnClickListener{
            popupMenu.show()
        }
    }
    private fun zoomOnMap(latlng:LatLng)
    {
        val newLatLngZoom=CameraUpdateFactory.newLatLngZoom(latlng,12f)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    private fun changeMap(itemId: Int) {
        when(itemId)
        {
            R.id.normal_map -> mGoogleMap?.mapType=GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mGoogleMap?.mapType=GoogleMap.MAP_TYPE_HYBRID
            R.id.satelliteMap -> mGoogleMap?.mapType=GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mGoogleMap?.mapType=GoogleMap.MAP_TYPE_TERRAIN
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap=googleMap
        //Adding a simple marker
        addMarker(LatLng(13.123,12.123))

        //Adding draggable Marker
        draggableMarker(LatLng(12.456,14.765))
        //Add custom Marker
       customMarker(R.drawable.flag,LatLng(13.999,12.456))

    }
    private fun addMarker(position: LatLng):Marker
    {
         val marker=  mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Marker")
         )
        return marker!!

    }
    private fun draggableMarker(position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Draggable Marker")
            .draggable(true))
    }
    private fun customMarker(icon:Int , position: LatLng)
    {
        mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Custom Marker")
            .icon(BitmapDescriptorFactory.fromResource(icon))
        )
    }
}