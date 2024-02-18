package com.example.map

import okhttp3.Interceptor
import okhttp3.Response

class MapInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
        println("Request: ${request.method()} ${request.url()}")

        val response = chain.proceed(request)

        // Log the response details
        println("Response: ${response.code()} ${response.message()}")

        return response
    }

}