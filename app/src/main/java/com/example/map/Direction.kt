//package com.example.map
//
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import com.android.volley.Request
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.PolylineOptions
//import com.google.maps.android.PolyUtil
//import org.json.JSONException
//import org.json.JSONObject
//
//
//
//class Direction: AppCompatActivity(), OnMapReadyCallback {
//    private lateinit var mMap: GoogleMap
//    private lateinit var apiInterface: API
//    private var polylineList = mutableListOf<LatLng>()
//    private lateinit var polylineOptions: PolylineOptions
//    private lateinit var route: com.example.map.Route
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//      setContentView(R.layout.activity_direction)
//        val mapFragment =
//            supportFragmentManager.findFragmentById(R.id.directionMap) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//      //  route=Route()
////        val okHttpClient = okhttp3.OkHttpClient.Builder()
////            .addInterceptor(MapInterceptor()) // Add your interceptor here
////            .build()
////        val retrofit = Retrofit.Builder()
////            .baseUrl("https://maps.googleapis.com/")
////            .addConverterFactory(GsonConverterFactory.create())
////            .client(okHttpClient)
////            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
////            .build()
////        apiInterface = retrofit.create(API::class.java)
//
//
//
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        mMap.isTrafficEnabled=true
//        val str_origin = "19.0760" + "," + "72.8777"//mumbai
//        val str_dest ="18.5204" + "," + "73.8567" //pune
////        Log.e("slo",str_origin)
////        Log.e("str_dest",str_dest)
//        setPolyLines(str_origin,str_dest)
//
//
//    }
//    fun setPolyLines(origin: String, destination: String) {
//
//        val url: String = getDirectionsUrl(origin, destination)
//
//        val queue = Volley.newRequestQueue(this)
//
//        val stringRequest = StringRequest(
//            Request.Method.GET, url,
//            { response ->
//                try {
//                    val jsonResponse = JSONObject(response)
//                    val routes = jsonResponse.getJSONArray("routes")
//                    if (routes.length() > 0) {
//                        val legs = routes.getJSONObject(0).getJSONArray("legs")
//                        if (legs.length() > 0) {
//                            val steps = legs.getJSONObject(0).getJSONArray("steps")
//                            val path: MutableList<List<LatLng>> = ArrayList()
//
//                            for (i in 0 until steps.length()) {
//                                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
//                                path.add(PolyUtil.decode(points))
//                            }
//
//                            for (i in path.indices) {
//                                mMap.addPolyline(
//                                    PolylineOptions()
//                                        .addAll(path[i])
//                                        .color(Color.BLUE)
//                                )
//                            }
//                        }
//                    }
//                } catch (e: JSONException) {
//                    throw RuntimeException(e)
//                }
//                // Get routes
//                Log.e("respone", response)
//            },
//            { error -> Log.e("err", error.message.toString()) }
//        )
//
//        queue.add(stringRequest)
//    }
//    private fun getDirectionsUrl(origin: String, destination: String): String {
////        val str_origin =
////            "origin=" + sourceLatLang.latitude + "," + sourceLatLang.longitude
////
////        // Destination of route
////
////        // Destination of route
////        val str_dest =
////            "destination=" + destinationLatlng.latitude + "," + destinationLatlng.longitude
//
//        // Sensor enabled
//
//        // Sensor enabled
//        val sensor = "sensor=false"
//        val mode = "mode=driving"
//
//        // Building the parameters to the web service
//
//        // Building the parameters to the web service
//        val parameters = "$origin&$destination&$sensor&$mode"
//
//        // Output format
//
//        // Output format
//        val output = "json"
//
//        // Building the url to the web service
//
//        // Building the url to the web service
//        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=" +getString(R.string.googleMapApiKey)
//    }
//
////    private fun getDirection(origin: String, destination: String){
////
////
////        val url: String = getDirectionsUrl(origin, destination)
////
////        val queue = Volley.newRequestQueue(this)
//
//
//}
//
////    private fun getDirectionsUrl(origin: String, destination: String): String {
////
////    }
//
////    private fun decodePolyline(encodedPath: String): List<LatLng> {
////        val poly = ArrayList<LatLng>()
////        var index = 0
////        val len = encodedPath.length
////        var lat = 0
////        var lng = 0
////        while (index < len) {
////            var result = 1
////            var shift = 0
////            var temp: Int
////            do {
////                temp = encodedPath[index++].toInt() - 63 - 1
////                result += temp shl shift
////                shift += 5
////            } while (temp >= 0x1f)
////            lat += if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
////            result = 1
////            shift = 0
////            do {
////                temp = encodedPath[index++].toInt() - 63 - 1
////                result += temp shl shift
////                shift += 5
////            } while (temp >= 0x1f)
////            lng += if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
////            poly.add(LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
////        }
////        return poly
////    }
//
