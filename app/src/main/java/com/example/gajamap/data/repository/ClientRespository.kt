package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.PostClientRequest
import com.example.gajamap.data.model.PostKakaoPhoneRequest
import com.example.gajamap.data.model.PutClientRequest
import com.example.gajamap.data.service.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Path

class ClientRespository {
    private val clientClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //고객 등록
    suspend fun postClient(clientName: RequestBody,
                           groupId : RequestBody,
                           phoneNumber : RequestBody,
                           mainAddress : RequestBody,
                           detail : RequestBody,
                           latitude : RequestBody,
                           longitude : RequestBody,
                           clientImage : MultipartBody.Part?,
    isBasicImage: RequestBody) = clientClient.postClient(clientName,groupId,phoneNumber, mainAddress, detail, latitude, longitude, clientImage, isBasicImage)

    //카카오, 전화번호부 데이터 등록
    suspend fun postKakaoPhoneClient(postKakaoPhoneRequest: PostKakaoPhoneRequest) = clientClient.postKakaoPhoneClient(postKakaoPhoneRequest)

    //고객 정보 변경
    suspend fun putClient(groupid : Int, clientId: Int,
                          clientName: RequestBody,
                          groupId : RequestBody,
                          phoneNumber : RequestBody,
                          mainAddress : RequestBody,
                          detail : RequestBody,
                          latitude : RequestBody,
                          longitude : RequestBody,
                          clientImage : MultipartBody.Part?,
                          isBasicImage: RequestBody) = clientClient.putClient(groupid, clientId, clientName,groupId,phoneNumber, mainAddress, detail, latitude, longitude, clientImage, isBasicImage)


    // 그룹 조회
    suspend fun checkGroup() = clientClient.checkGroup()

    suspend fun logout() = clientClient.logout()

    suspend fun withdraw() = clientClient.withdraw()
}