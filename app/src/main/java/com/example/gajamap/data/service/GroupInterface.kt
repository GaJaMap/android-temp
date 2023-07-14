package com.example.gajamap.data.service

import com.example.gajamap.data.response.CheckGroupResponse
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.response.CreateGroupResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface GroupInterface {
    // 그룹 생성
    @POST("/api/group")
    suspend fun createGroup(@Body createGroupRequest: CreateGroupRequest) : Response<CreateGroupResponse>

    // 그룹 조회
    @GET("/api/group/?page=0")
    suspend fun checkGroup() : Response<CheckGroupResponse>

    // 그룹 삭제
    @DELETE("/api/group/{groupId}")
    suspend fun deleteGroup(@Path("groupId") groupId : Int) : Response<CreateGroupResponse>
}