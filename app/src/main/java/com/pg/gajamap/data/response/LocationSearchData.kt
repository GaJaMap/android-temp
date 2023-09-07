package com.pg.gajamap.data.response

data class LocationSearchData (
    val name: String,      // 장소명
    val road: String,      // 도로명 주소
    val address: String,   // 지번 주소 (사용하지 않을 것 같긴 하지만 일단 추가)
    val x: Double,         // 경도(Longitude)
    val y: Double          // 위도(Latitude)
)