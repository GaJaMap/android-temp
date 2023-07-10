package com.example.gajamap.data.repository

import androidx.appcompat.resources.Compatibility.Api15Impl
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.LoginRequest
import com.example.gajamap.data.service.ApiInterface

class LoginRepository {
    private val loginClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //로그인
    suspend fun postLogin(loginRequest: LoginRequest) = loginClient.postLogin(loginRequest)
}