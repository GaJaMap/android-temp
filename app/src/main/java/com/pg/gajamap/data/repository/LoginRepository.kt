package com.pg.gajamap.data.repository

import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.data.model.LoginRequest
import com.pg.gajamap.data.service.ApiInterface

class LoginRepository {
    private val loginClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //로그인
    suspend fun postLogin(loginRequest: LoginRequest) = loginClient.postLogin(loginRequest)
    suspend fun autoLogin() = loginClient.autoLogin()

}