package com.example.gajamap.base

import android.app.Application
import com.example.gajamap.BuildConfig

import com.kakao.sdk.common.KakaoSdk

class GajaMapApplication : Application() {

    companion object {
        lateinit var instance: GajaMapApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "${BuildConfig.KAKAO_API_KEY}")
        instance = this
    }
}