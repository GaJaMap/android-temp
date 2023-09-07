package com.pg.gajamap.data.service

import com.pg.gajamap.data.model.GetAllClientResponse
import com.pg.gajamap.data.response.CheckGroupResponse
import com.pg.gajamap.data.response.CreateGroupRequest
import com.pg.gajamap.data.response.GroupResponse
import retrofit2.Response
import retrofit2.http.*

interface GroupInterface {
    // 그룹 생성
    @POST("/api/group")
    suspend fun createGroup(@Body createGroupRequest: CreateGroupRequest) : Response<Long>

    // 그룹 조회
    @GET("/api/group/?page=0")
    suspend fun checkGroup() : Response<CheckGroupResponse>

    // 그룹 삭제
    @DELETE("/api/group/{groupId}")
    suspend fun deleteGroup(@Path("groupId") groupId : Long) : Response<GroupResponse>

    // 그룹 수정
    @PUT("/api/group/{groupId}")
    suspend fun modifyGroup(@Path("groupId") groupId : Long, @Body createGroupRequest: CreateGroupRequest) : Response<GroupResponse>

    // 특정 그룹내에 고객
    @GET("/api/group/{groupId}/clients")
    suspend fun getGroupAllClient(@Path("groupId")groupId : Long) : Response<GetAllClientResponse>

    // 특정 그룹 내 고객 검색 -> 조회할 고객 이름 검색
    @GET("/api/group/{groupId}/clients")
    suspend fun getGroupAllClientName(@Path("groupId")groupId : Long, @Query("wordCond")wordCond : String) : Response<GetAllClientResponse>
}