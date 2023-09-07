package com.pg.gajamap.data.model


data class LoginRequest(
    val accessToken : String = ""
)

data class LoginResponse(
    val email : String,
    val authority : String,
    val createdDate : String
)

