package com.example.clockingo.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://flexiqueryapidev-b6bvgshxepapevb4.centralus-01.azurewebsites.net"
    private const val API_KEY = "$%DUF4C0G4$%"

    private val client = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(API_KEY))
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val entryApi: EntryApi by lazy {
        retrofit.create(EntryApi::class.java)
    }

    val exitApi: ExitApi by lazy {
        retrofit.create(ExitApi::class.java)
    }

    val locationApi: LocationApi by lazy {
        retrofit.create(LocationApi::class.java)
    }

    val roleApi: RoleApi by lazy {
        retrofit.create(RoleApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
}