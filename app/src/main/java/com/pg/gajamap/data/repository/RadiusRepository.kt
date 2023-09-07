package com.pg.gajamap.data.repository

import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.data.service.RadiusInterface

class RadiusRepository {
    private val radius = GajaMapApplication.sRetrofit.create(RadiusInterface::class.java)

    // 전체 고객 대상 반경 검색
    suspend fun wholeRadius(radius1: Int, latitude: Double, longitude: Double) = radius.wholeRadius(radius1, latitude, longitude)

    // 특정 그룹 내에 고객 반경 검색
    suspend fun specificRadius(groupId : Long, radius1: Int, latitude: Double, longitude: Double) = radius.specificRadius(groupId, radius1, latitude, longitude)
}