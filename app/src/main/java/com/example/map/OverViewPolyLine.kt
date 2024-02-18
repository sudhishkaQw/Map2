package com.example.map

import com.squareup.okhttp.Request
import java.io.IOException

class OverViewPolyLine {
    private var points:String?=null
    fun getPoints(): String? {
        return points
    }

    fun setPoints(points: String?) {
        this.points= points.toString()
    }


}