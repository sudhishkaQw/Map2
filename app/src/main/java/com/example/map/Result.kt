package com.example.map

import com.squareup.okhttp.Route

class Result {
    private var routes: List<Route>? =null
    private var status: String? = null

    fun getRoutes(): List<Route>? {
        return routes
    }

    fun setRoutes(routes: List<Route>?) {
        this.routes = routes
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }
}