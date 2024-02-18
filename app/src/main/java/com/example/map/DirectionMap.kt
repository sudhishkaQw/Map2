package com.example.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONException
import org.json.JSONObject

class DirectionMap : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direction)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.directionMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var zoomIn: ImageButton =findViewById(R.id.zoomInButtonDir)
        zoomIn.setOnClickListener { view->
            onZoomInClicked(view)
        }
        var zoomOut: ImageButton =findViewById(R.id.zoomOutButtonDir)
        zoomOut.setOnClickListener { view->
            onZoomOutClicked(view)
        }
        var sourceEditText:EditText=findViewById(R.id.sourceEditText)
        var destinationEditText:EditText=findViewById(R.id.destinationEditText)

        var direction:Button = findViewById(R.id.searchDir)
        direction.setOnClickListener {
            val origin = sourceEditText.text.toString()
            val destination = destinationEditText.text.toString()
            setPolyLines(origin, destination)
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isTrafficEnabled=true
//        val str_origin = "19.0760" + "," + "72.8777"//mumbai
//        val str_dest ="18.5204" + "," + "73.8567" //pune

//        Log.e("slo",str_origin)
//        Log.e("str_dest",str_dest)
    //    setPolyLines(str_origin,str_dest)
    }
    fun setPolyLines(origin: String, destination: String) {

        val url: String = getDirectionsUrl(origin, destination)

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
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
                    throw RuntimeException(e)
                }
                // Get routes
                Log.e("response", response)
            },
            { error -> Log.e("err", error.message.toString()) }
        )

        queue.add(stringRequest)
    }
    private fun getDirectionsUrl(origin: String, destination: String): String {
        val str_origin = "origin=$origin"
//
//        // Destination of route
//
//        // Destination of route
        val str_dest = "destination=$destination"


        val sensor = "sensor=false"
        val mode = "mode=driving"


        val parameters = "$str_origin&$str_dest&$sensor&$mode"

        val output = "json"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=" +getString(R.string.googleMapApiKey)
    }
    fun onZoomInClicked(view: View) {
        mMap?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    fun onZoomOutClicked(view: View) {
        mMap?.animateCamera(CameraUpdateFactory.zoomOut())
    }


}