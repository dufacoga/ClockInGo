package com.example.clockingo.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("x-API-KEY", apiKey)
            .build()
        return chain.proceed(request)
    }
}