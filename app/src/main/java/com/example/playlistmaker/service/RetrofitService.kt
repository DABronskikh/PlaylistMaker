package com.example.playlistmaker.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private const val API_BASE_URL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getTrackApiService(): TrackApiService {
        return retrofit.create(TrackApiService::class.java)
    }

}
