package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.PostClientRequest
import com.example.gajamap.data.model.PostKakaoPhoneRequest
import com.example.gajamap.data.model.PutClientRequest
import com.example.gajamap.data.service.ApiInterface

class ClientRespository {
    private val clientClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //고객 등록
    suspend fun postClient(postClientRequest: PostClientRequest) = clientClient.postClient(postClientRequest)

    //카카오, 전화번호부 데이터 등록
    suspend fun postKakaoPhoneClient(postKakaoPhoneRequest: PostKakaoPhoneRequest) = clientClient.postKakaoPhoneClient(postKakaoPhoneRequest)

    //고객 삭제
    suspend fun deleteClient(groupId : Int, client : Int) = clientClient.deleteClient(groupId, client)

    //고객 정보 변경
    suspend fun putClient(putClientRequest: PutClientRequest, groupId : Int, client : Int) = clientClient.putClient(putClientRequest, groupId, client)

}