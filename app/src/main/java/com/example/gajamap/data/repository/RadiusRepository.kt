package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.RadiusRequest
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.service.RadiusInterface

class RadiusRepository {
    private val radius = GajaMapApplication.sRetrofit.create(RadiusInterface::class.java)

    // 전체 고객 대상 반경 검색
    suspend fun wholeRadius(radiusRequest: RadiusRequest) = radius.wholeRadius(radiusRequest)
}