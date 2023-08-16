package com.example.gajamap.data.service

import com.example.gajamap.data.model.KakaoMapImage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ImgKakaoService {
    @GET("v2/maps/staticmap")
    fun getMapImage(
        @Query("center") center: String,
        @Query("width") width: Int,
        @Query("height") height: Int,
        @Query("level") level: Int,
        @Query("api_key") apiKey: String
    ): Call<KakaoMapImage>
}