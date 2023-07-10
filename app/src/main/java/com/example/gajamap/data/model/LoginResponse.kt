package com.example.gajamap.data.model


data class LoginResponse(
    val message : Int = -1
)

data class LoginRequest(
    val accessToken : String = ""
)
