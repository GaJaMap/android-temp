package com.pg.gajamap.data.service

import com.pg.gajamap.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    //로그인
    @POST("/api/user/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest) : Response<LoginResponse>

    // 특정 그룹내에 특정 고객 조회
    @GET("/api/group/{groupId}/clients/{clientId}")
    suspend fun getGroupClient(@Path("groupId")groupId : Long, @Path("clientId")clientId: Long) : Response<GetGroupClientResponse>

    //전체 고객 검색
    @GET("/api/clients")
    suspend fun getAllClient() : Response<GetAllClientResponse>

    //전체 고객 검색 -> 조회할 고객 이름 검색
    @GET("/api/clients")
    suspend fun getAllClientName(@Query("wordCond")wordCond : String) : Response<GetAllClientResponse>

    //특정 그룹내에 고객 전부 조회 -> 이름 검색
    @GET("/api/group/{groupId}/clients")
    suspend fun getGroupAllClientName(@Path("groupId")groupId : Long, @Query("wordCond")wordCond : String) : Response<GetGroupAllClientResponse>

    //특정 그룹내에 고객
    @GET("/api/group/{groupId}/clients")
    suspend fun getGroupAllClient(@Path("groupId")groupId : Long) : Response<GetGroupAllClientResponse>

    //고객 등록
    @Multipart
    @POST("/api/clients")
    suspend fun postClient(@Part("clientName")clientName: RequestBody,
                           @Part("groupId") groupId : RequestBody,
                           @Part("phoneNumber") phoneNumber : RequestBody,
                           @Part("mainAddress") mainAddress : RequestBody,
                           @Part("detail") detail : RequestBody,
                           @Part("latitude") latitude : RequestBody,
                           @Part("longitude") longitude : RequestBody,
                           @Part clientImage : MultipartBody.Part?,
                           @Part("isBasicImage") isBasicImage : RequestBody
    ) : Response<Client>

    //카카오, 전화번호부 데이터 등록
    @POST("api/clients/bulk")
    suspend fun postKakaoPhoneClient(@Body kakaoPhoneRequest: PostKakaoPhoneRequest) : Response<List<Int>>

    //고객 삭제
    @DELETE("/api/group/{groupId}/clients/{clientId}")
    suspend fun deleteClient(@Path("groupId")groupId : Long, @Path("clientId")clientId: Long) : Response<BaseResponse>

    //고객 정보 변경
    @Multipart
    @PUT("/api/group/{groupId}/clients/{clientId}")
    suspend fun putClient(
        @Path("groupId")groupId : Long, @Path("clientId")clientId: Long,
        @Part("clientName") clientName: RequestBody,
        @Part("group") group : RequestBody,
        @Part("phoneNumber") phoneNumber : RequestBody,
        @Part("mainAddress") mainAddress : RequestBody,
        @Part("detail") detail : RequestBody,
        @Part("latitude") latitude : RequestBody,
        @Part("longitude") longitude : RequestBody,
        @Part clientImage : MultipartBody.Part?,
        @Part("isBasicImage") isBasicImage : RequestBody
    ): Response<Client>


    // 다수 고객 삭제
    @POST("/api/group/{groupId}/clients/bulk-delete")
    suspend fun deleteAnyClient(@Path("groupId")groupId : Long, @Body deleteRequest: DeleteRequest) : Response<BaseResponse>


    // 전체 고객 반경 검색 - 이름
    @GET("/api/clients/nearby")
    suspend fun allNameRadius(@Query("wordCond")wordCond: String, @Query("radius") radius: Double, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<GetRadiusResponse>

    // 전체 고객 반경 검색
    @GET("/api/clients/nearby")
    suspend fun allRadius(@Query("radius") radius: Double, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<GetRadiusResponse>

    //특정 그룹내에 고객 반경 검색 - 이름
    @GET("/api/group/{groupId}/clients/nearby")
    suspend fun groupNameRadius(@Path("groupId") groupId : Long, @Query("wordCond")wordCond: String, @Query("radius") radius: Double, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<GetRadiusResponse>

    //특정 그룹내에 고객 반경 검색 - 이름
    @GET("/api/group/{groupId}/clients/nearby")
    suspend fun groupRadius(@Path("groupId") groupId : Long, @Query("radius") radius: Double, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double) : Response<GetRadiusResponse>

    // 그룹 조회
    @GET("/api/group/?page=0")
    suspend fun checkGroup() : Response<GroupResponse>

    //로그아웃 요청
    @POST("/api/user/logout")
    suspend fun logout() : Response<BaseResponse>

    //회원탈퇴
    @DELETE("/api/user")
    suspend fun withdraw() : Response<BaseResponse>

    //자동로그인
    @GET("/api/user/auto-login")
    suspend fun autoLogin() : Response<AutoLoginResponse>
}