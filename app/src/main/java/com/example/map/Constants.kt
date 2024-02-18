package com.example.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlin.random.Random

object Constants {
    fun getHeatMapOfData():ArrayList<LatLng>
    {
        val data =ArrayList<LatLng>()
        for(i in 1..200)
        {
            val latitude= Random.nextDouble(50.0,75.0)
            val longitude= Random.nextDouble(50.0,75.0)
            data.add(LatLng(latitude,longitude))

        }
        return data
    }
    fun getWeightedHeatMapOfData():ArrayList<WeightedLatLng>
    {
        val data =ArrayList<WeightedLatLng>()
        for(i in 1..200)
        {
            val latitude= Random.nextDouble(50.0,75.0)
            val longitude= Random.nextDouble(50.0,75.0)
            val intensity=Random.nextDouble(1.0,1000.0)
            data.add(WeightedLatLng(LatLng(latitude,longitude),intensity))

        }
        return data
    }
}