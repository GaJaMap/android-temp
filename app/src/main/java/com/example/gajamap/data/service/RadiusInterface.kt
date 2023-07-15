package com.example.gajamap.data.service

import com.example.gajamap.data.model.RadiusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RadiusInterface {
    // 전체 고객 대상 반경 검색
    @GET("/api/clients/nearby")
    suspend fun wholeRadius(@Query("radius") radius: Double, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<RadiusResponse>
}