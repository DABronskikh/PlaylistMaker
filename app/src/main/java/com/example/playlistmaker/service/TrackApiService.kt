package com.example.playlistmaker.service

import com.example.playlistmaker.data.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {

    @GET("/search")
    fun search(@Query("term") text: String): Call<TracksResponse>

}
