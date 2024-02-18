package com.example.map

import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("maps/api/directions/json")
    fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("sensor")sensor:Boolean,
        @Query("key") key: String
    ): Observable<Result>

}