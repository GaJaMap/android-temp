package com.pg.gajamap.base


import android.content.Context
import android.content.SharedPreferences
import com.pg.gajamap.data.model.AutoLoginResponse
import com.google.gson.Gson

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String?): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    companion object {
        private const val KEY_AUTO_LOGIN_RESPONSE = "autoLoginResponse"
    }

    private val gson = Gson()

    fun saveAutoLoginResponse(autoLoginResponse: AutoLoginResponse) {
        val json = gson.toJson(autoLoginResponse)
        prefs.edit().putString(KEY_AUTO_LOGIN_RESPONSE, json).apply()
    }

    fun getAutoLoginResponse(): AutoLoginResponse? {
        val json = prefs.getString(KEY_AUTO_LOGIN_RESPONSE, null)
        return gson.fromJson(json, AutoLoginResponse::class.java)
    }

    // SharedPreferences 초기화 메서드 추가
    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }

}