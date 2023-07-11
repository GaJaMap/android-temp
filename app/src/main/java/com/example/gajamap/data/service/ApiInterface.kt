package com.example.gajamap.data.service

import com.example.gajamap.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    //로그인
    @POST("/api/user/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest) : Response<Long>

    // 특정 그룹내에 특정 고객 조회
    @GET("/api/group/{groupId}/clients/{clientId}")
    suspend fun getGroupClient(@Path("groupId")groupId : Int, @Path("clientId")clientId: Int) : Response<GetGroupClientResponse>

    //전체 고객 검색
    @GET("/api/clients")
    suspend fun getAllClient() : Response<GetAllClientResponse>

    //전체 고객 검색 -> 조회할 고객 이름 검색
    @GET("/api/clients")
    suspend fun getAllClientName(@Query("wordCond")wordCond : String) : Response<GetAllClientResponse>


    //특정 그룹내에 고객 전부 조회
    @GET("/api/group/{groupId}/clients")
    suspend fun getGroupAllClient(@Query("wordCond")wordCond : String, @Path("groupId")groupId : Int) : Response<GetGroupAllClientResponse>

    //고객 등록
    @POST("/api/clients")
    suspend fun postClient(@Body postClientRequest: PostClientRequest) : Response<Int>

    //카카오, 전화번호부 데이터 등록
    @POST("api/clients/bulk")
    suspend fun postKakaoPhoneClient(@Body postKakaoPhoneRequest: PostKakaoPhoneRequest) : Response<List<Int>>

    //고객 삭제
    @DELETE("/api/group/{groupId}/clients/{clientId}")
    suspend fun deleteClient(@Path("groupId")groupId : Int, @Path("clientId")clientId: Int) : Response<BaseResponse>

    //고객 정보 변경
    @PUT("/api/group/{groupId}/clients/{clientId}")
    suspend fun putClient(@Body putClientRequest: PutClientRequest, @Path("groupId")groupId : Int, @Path("clientId")clientId: Int): Response<BaseResponse>
}