package com.pg.gajamap.data.service

import com.pg.gajamap.data.model.RadiusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadiusInterface {
    // 전체 고객 대상 반경 검색
    @GET("/api/clients/nearby")
    suspend fun wholeRadius(@Query("radius") radius: Int, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<RadiusResponse>

    // 그룹 고객 대상 반경 검색
    @GET("/api/group/{groupId}/clients/nearby")
    suspend fun specificRadius(@Path("groupId") groupId : Long, @Query("radius") radius: Int, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<RadiusResponse>
}