package com.example.map

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AppPermission {
    fun isStorageOk(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

//    fun requestStoragePermission(activity: Activity) {
//        ActivityCompat.requestPermissions(
//            activity,
//            arrayOf(
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ),
//            Constants.STORAGE_REQUEST_CODE
//        )
//    }

    fun isLocationOk(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

//    fun requestLocationPermission(activity: Activity) {
//        ActivityCompat.requestPermissions(
//            activity,
//            arrayOf(
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            ),
//            AllConstant.LOCATION_REQUEST_CODE
//        )
//    }
}
