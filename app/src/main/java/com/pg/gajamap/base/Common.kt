package com.pg.gajamap.base

import com.pg.gajamap.data.response.CheckGroupResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//group data 저장
fun setPref(key:String, values: List<CheckGroupResponse>){
    val gson = Gson()
    val json = gson.toJson(values)
    GajaMapApplication.prefs.setString(key, json)
}

//group data 저장 값 불러옴
fun getPref(key: String): List<CheckGroupResponse> {
    var json = GajaMapApplication.prefs.getString(key, null)
    if(GajaMapApplication.prefs.getString(key, null).toString() != null){
        json = GajaMapApplication.prefs.getString(key, null)
    }
    val gson = Gson()

    return gson.fromJson(
        json,
        object : TypeToken<List<CheckGroupResponse?>>() {}.type
    )
}