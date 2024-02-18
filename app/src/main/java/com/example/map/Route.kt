package com.example.map

class Route {
    private var overviewPolyline: OverViewPolyLine? = null

    fun getOverviewPolyline(): OverViewPolyLine? {
        return overviewPolyline
    }

    fun setOverviewPolyline(overviewPolyline: OverViewPolyLine) {
        this.overviewPolyline = overviewPolyline
    }
}