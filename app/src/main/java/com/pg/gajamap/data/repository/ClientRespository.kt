package com.pg.gajamap.data.repository

import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.data.model.PostKakaoPhoneRequest
import com.pg.gajamap.data.service.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
    suspend fun putClient(groupId : Long, clientId: Long,
                          clientName: RequestBody,
                          group : RequestBody,
                          phoneNumber : RequestBody,
                          mainAddress : RequestBody,
                          detail : RequestBody,
                          latitude : RequestBody,
                          longitude : RequestBody,
                          clientImage : MultipartBody.Part?,
                          isBasicImage: RequestBody) = clientClient.putClient(groupId, clientId, clientName,group,phoneNumber, mainAddress, detail, latitude, longitude, clientImage, isBasicImage)


    // 그룹 조회
    suspend fun checkGroup() = clientClient.checkGroup()

    suspend fun logout() = clientClient.logout()

    suspend fun withdraw() = clientClient.withdraw()
}