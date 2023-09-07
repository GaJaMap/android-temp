package com.pg.gajamap.data.model

data class ViewPagerData(
    val profileImg: String,
    var name: String,
    var address: String,
    var phoneNumber: String,
    var distance: Double?=null
)
