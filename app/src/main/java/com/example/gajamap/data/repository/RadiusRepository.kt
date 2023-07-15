package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.service.RadiusInterface

class RadiusRepository {
    private val radius = GajaMapApplication.sRetrofit.create(RadiusInterface::class.java)

    // 전체 고객 대상 반경 검색
    suspend fun wholeRadius(radius1: Double, latitude: Double, longitude: Double) = radius.wholeRadius(radius1, latitude, longitude)

    // 특정 그룹 내에 고객 반경 검색
    suspend fun specificRadius(radius1: Double, latitude: Double, longitude: Double, groupId : Long) = radius.specificRadius(radius1, latitude, longitude, groupId)
}