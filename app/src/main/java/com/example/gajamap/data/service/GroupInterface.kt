package com.example.gajamap.data.service

import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.response.CreateGroupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupInterface {
    // 그룹 생성
    @POST("/api/group")
    suspend fun createGroup(@Body createGroupRequest: CreateGroupRequest) : Response<CreateGroupResponse>
}