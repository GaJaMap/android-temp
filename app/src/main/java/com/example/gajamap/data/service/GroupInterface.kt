package com.example.gajamap.data.service

import com.example.gajamap.data.response.CheckGroupResponse
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.response.CreateGroupResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GroupInterface {
    // 그룹 생성
    @POST("/api/group")
    suspend fun createGroup(@Body createGroupRequest: CreateGroupRequest) : Response<CreateGroupResponse>

    // 그룹 조회
    @GET("/api/group/?page=0")
    suspend fun checkGroup() : Response<CheckGroupResponse>
}