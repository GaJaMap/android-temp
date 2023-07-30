package com.example.gajamap.data.model


data class LoginRequest(
    val accessToken : String = ""
)

data class LoginResponse(
    val groupId : Int,
    val authority : String
)

