package com.example.map
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Layout.Directions
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.model.DirectionsResult
import com.google.maps.model.PlusCode
import com.google.maps.model.TravelMode
import org.joda.time.DateTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() , OnMapReadyCallback {
    private var mGoogleMap:GoogleMap? = null
    private  var currentLocation: Location? = null
    private var marker: Marker? = null
    private val REQUEST_CODE = 101
    private lateinit var geoApiContext: GeoApiContext
    private  val PLACE_REQUEST_PICKER=1
    private lateinit var clusterManager: ClusterManager<MyClusterItem>
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private var fusedClient: FusedLocationProviderClient? = null
    //  private lateinit var autoCompleteFragment:AutocompleteSupportFragment
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, getString(R.string.googleMapApiKey))
        // Set custom info window adapter

        val initialPosition = LatLng(37.7749, -122.4194) // For example: San Francisco

        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 12f))

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
            geoApiContext = GeoApiContext.Builder()
                .queryRateLimit(3)
                .apiKey(getString(R.string.googleMapApiKey))
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build()

            // Call your function to fetch directions and perform map operations
        }
        val mapOptionButton : ImageButton=findViewById(R.id.mapOptionMenu)
        val popupMenu=PopupMenu(this,mapOptionButton)
        popupMenu.menuInflater.inflate(R.menu.map_option,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem->
            changeMap(menuItem.itemId)
            true
        }
        searchView = findViewById(R.id.search)
        var picker:ImageButton=findViewById(R.id.pin)
       picker.setOnClickListener {
           startActivity(Intent(this, MapActivity::class.java))
       }
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation()
        mapOptionButton.setOnClickListener {
            popupMenu.show()
        }
        if (Places.isInitialized()) {
            Places.initialize(applicationContext, R.string.googleMapApiKey.toString())
        }
        var streetview:ImageButton=findViewById(R.id.streetView)
        streetview.setOnClickListener {
            startActivity(Intent(this, StreetView::class.java))
        }
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val loc = searchView!!.query.toString()
                if (loc.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Location Not Found", Toast.LENGTH_SHORT).show()
                } else {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        val addressList = geocoder.getFromLocationName(loc,5)
                        if (addressList!!.isNotEmpty()) {
                            Log.e("location",addressList.size.toString())
                            Log.e("addressList",addressList.toString())
                            val latLng = LatLng(addressList?.get(0)!!.latitude, addressList[0].longitude)
                            marker?.remove()
                            val markerOptions = MarkerOptions().position(latLng).title(loc)
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5f)
                            mGoogleMap?.animateCamera(cameraUpdate)
                            marker = mGoogleMap?.addMarker(markerOptions)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        var zoomIn:ImageButton=findViewById(R.id.zoomInButton)
        zoomIn.setOnClickListener { view->
            onZoomInClicked(view)
        }
        var zoomOut:ImageButton=findViewById(R.id.zoomOutButton)
        zoomOut.setOnClickListener { view->
            onZoomOutClicked(view)
        }
        var direction:ImageButton=findViewById(R.id.direction)
        direction.setOnClickListener {
            startActivity(Intent(this, DirectionMap::class.java))
        }
        var route:ImageButton=findViewById(R.id.route)
        route.setOnClickListener {
            startActivity(Intent(this,UserRoute::class.java))
        }


    }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
{
    super.onActivityResult(requestCode, resultCode, data)
    if(requestCode==PLACE_REQUEST_PICKER)
    {
        if(resultCode== RESULT_OK)
        {
            var place: Place? =PlacePicker.getPlace(data,this@MainActivity)
            val latitude = place!!.latLng.latitude.toString()
            val longitude = place.latLng.longitude.toString()
            val stringBuilder=StringBuilder()
            stringBuilder.append("Latitude:")
            stringBuilder.append(latitude)
            stringBuilder.append("/n")
            stringBuilder.append("Longitude:")
            stringBuilder.append(longitude)
        }
    }
}
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }
        val task: Task<Location> = fusedClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                // Toast.makeText(applicationContext, "${currentLocation.latitude}${currentLocation.longitude}", Toast.LENGTH_SHORT).show()
                val supportMapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
                supportMapFragment?.getMapAsync(this@MainActivity)
            }
        }
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
    fun onZoomInClicked(view: View) {
        mGoogleMap?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    fun onZoomOutClicked(view: View) {
        mGoogleMap?.animateCamera(CameraUpdateFactory.zoomOut())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap=googleMap
        val latLng = currentLocation?.latitude?.let { LatLng(it, currentLocation!!.longitude) }
        val markerOptions = latLng?.let { MarkerOptions().position(it).title("My Current Location") }
        latLng?.let { CameraUpdateFactory.newLatLng(it) }
            ?.let { googleMap.animateCamera(it) }
        latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 5f) }
            ?.let { googleMap.animateCamera(it) }
        markerOptions?.let { googleMap.addMarker(it) }
        val sourceLatLng = LatLng(-37.81319, 144.96298)
        val destinationLatLng = LatLng(-31.95285, 115.85734)
        // Draw polyline between source and destination
        val mode = "driving"
        clusterManager = ClusterManager(this, mGoogleMap)
        mGoogleMap?.setOnCameraIdleListener(clusterManager)
        mGoogleMap?.setOnMarkerClickListener(clusterManager)

        addClusterItems() // Add your cluster items here
        clusterManager.cluster()
        clusterManager.clusterMarkerCollection// Perform clustering

        // Move camera to a default position
        val defaultLocation = LatLng(37.7749, -122.4194)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
       // drawPolyline(sourceLatLng, destinationLatLng,mode)
//        //Adding a simple marker
//        addMarker(LatLng(13.123,12.123))
//
        //Adding draggable Marker
        addHeatMap()

        draggableMarker(LatLng(12.456,14.765))
//        //Add custom Marker
//       customMarker(R.drawable.flag,LatLng(13.999,12.456))

      //  addMarkersForPlaces()

    }
    private fun addClusterItems() {
        // Add your cluster items here
        val clusterItems = listOf(
            MyClusterItem(LatLng(37.7749, -122.4194), "Marker 1"),
            MyClusterItem(LatLng(37.7749, -122.4189), "Marker 2"),
            MyClusterItem(LatLng(37.7749, -122.4184), "Marker 3"),
            MyClusterItem(LatLng(37.7754, -122.4194), "Marker 4"),
            MyClusterItem(LatLng(37.7754, -122.4189), "Marker 5"),
            MyClusterItem(LatLng(37.7754, -122.4184), "Marker 6")
        )
        val clusterItem2= listOf(
            MyClusterItem( LatLng(40.7128, -74.0060),"New York"),
            MyClusterItem(LatLng(34.0522, -118.2437),"Los Angeles"),
            MyClusterItem(LatLng(41.8781, -87.6298),"Chicago"),
            MyClusterItem(LatLng(29.7604, -95.3698),"Houston"),
            MyClusterItem( LatLng(25.7617, -80.1918),"Miami")
        )

        clusterManager.addItems(clusterItems)
        clusterManager.addItems(clusterItem2)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }
////    private fun drawPolyline(origin: LatLng, destination: LatLng, mode: String) {
//        GlobalScope.launch {
////            try {
////                val directionsResult = getDirectionsResult(origin, destination, mode)
////                val decodedPath = decodePolyline(directionsResult.routes[0].overviewPolyline.encodedPath)
////                runOnUiThread {
////                    mGoogleMap?.addPolyline(PolylineOptions().addAll(decodedPath))
////                    mGoogleMap?.addMarker(MarkerOptions().position(origin).title("Origin"))
////                    mGoogleMap?.addMarker(MarkerOptions().position(destination).title("Destination"))
////                    mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 8f))
////                }
////            } catch (e: Exception) {
////                Log.e("Error", "Failed to get directions: ${e.message}")
////            }
////        }
////    }
    private fun addMarkersToMap(results: DirectionsResult?, mMap: GoogleMap) {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    results!!.routes[0].legs[0].startLocation.lat,
                    results!!.routes[0].legs[0].startLocation.lng
                )
            ).title(results!!.routes[0].legs[0].startAddress)
        )
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    results.routes[0].legs[0].endLocation.lat,
                    results.routes[0].legs[0].endLocation.lng
                )
            ).title(results.routes[0].legs[0].startAddress)
                .snippet(getEndLocationTitle(results))
        )
    }

    private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time: ${results.routes[0].legs[0].duration.humanReadable} Distance: ${results.routes[0].legs[0].distance.humanReadable}"
    }

    private fun addPolyline(results: DirectionsResult, mMap: GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.encodedPath)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath))
    }

    private  fun getDirectionsResult(origin: LatLng, destination: LatLng, mode: String): DirectionsResult {
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(R.string.googleMapApiKey.toString())
            .build()
        return DirectionsApi.newRequest(geoApiContext)
            .mode(com.google.maps.model.TravelMode.valueOf(mode.toUpperCase()))
            .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
            .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
            .await()
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
    private var circle:Circle?=null
private fun addHeatMap()
{
    val heatMapProvider=HeatmapTileProvider.Builder()
        .weightedData(Constants.getWeightedHeatMapOfData())
        .radius(20)
        .maxIntensity(1000.0)
        .build()
    mGoogleMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
}


}






