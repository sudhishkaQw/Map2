package com.example.map

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaFragment
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation
import com.google.android.gms.maps.model.StreetViewSource
class StreetView : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {
    private lateinit var streetViewPanorama: StreetViewPanorama
    private var secondLocation:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)
        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this)
        var button:ImageButton=findViewById(R.id.parnoramaButton)
        button.setOnClickListener {
            secondLocation!=secondLocation
            onStreetViewPanoramaReady(streetViewPanorama)
        }
    }

    override fun onStreetViewPanoramaReady(streetViewPanorama: StreetViewPanorama) {
        streetViewPanorama.setPosition(LatLng(51.52887, -0.1726073), StreetViewSource.OUTDOOR)
        if (secondLocation != secondLocation) {
            streetViewPanorama.setPosition(LatLng(51.52887, -0.1726073), StreetViewSource.OUTDOOR)
        } else {
            streetViewPanorama.setPosition(LatLng(51.52887, -0.1726073))
            streetViewPanorama.isStreetNamesEnabled = true
            streetViewPanorama.isPanningGesturesEnabled = true
            streetViewPanorama.isZoomGesturesEnabled = true
            streetViewPanorama.isUserNavigationEnabled = true
            streetViewPanorama.animateTo(
                StreetViewPanoramaCamera.Builder()
                    .orientation(StreetViewPanoramaOrientation(20.0f, 20.0f))
                    .zoom(streetViewPanorama.panoramaCamera.zoom)
                    .build(),
                2000
            )
            streetViewPanorama.setOnStreetViewPanoramaChangeListener(panoramaChangeListener)
            streetViewPanorama.setOnStreetViewPanoramaChangeListener {
                Toast.makeText(this, "LocationUpdated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val panoramaChangeListener = StreetViewPanorama.OnStreetViewPanoramaChangeListener {
        Toast.makeText(this, "LocationUpdated", Toast.LENGTH_SHORT).show()
    }

}
