package com.example.gajamap.data.service

import com.example.gajamap.data.model.LoginRequest
import com.example.gajamap.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    //로그인
    @POST("/api/user/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest) : Response<LoginResponse>
}