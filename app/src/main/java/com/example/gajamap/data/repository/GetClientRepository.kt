package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.DeleteRequest
import com.example.gajamap.data.service.ApiInterface

class GetClientRepository {

    private val getClientClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //특정 그룹내에 특정 고객 조회
    suspend fun getGroupClient(groupId : Int, client : Int) = getClientClient.getGroupClient(groupId, client)

    //전체 고객 검색
    suspend fun getAllClient() = getClientClient.getAllClient()

    //전체 고객 검색 -> 조회할 고객 이름 검색
    suspend fun getAllClientName(wordCond : String) = getClientClient.getAllClientName(wordCond)

    //특정 그룹내에 고객 전부 조회 -> 이름 검색
    suspend fun getGroupAllClientName(groupId : Int , wordCond : String) = getClientClient.getGroupAllClientName(groupId, wordCond)

    //특정 그룹내에 고객 전부 조회
    suspend fun getGroupAllClient(groupId : Int) = getClientClient.getGroupAllClient(groupId)

    //고객 삭제
    suspend fun deleteClient(groupId : Int, client : Int) = getClientClient.deleteClient(groupId, client)

    suspend fun deleteAnyClient(groupId : Int, deleteRequest: DeleteRequest) = getClientClient.deleteAnyClient(groupId, deleteRequest)

    //전체 반경 - 이름
    suspend fun allNameRadius(wordCond : String, radius: Double, latitude: Double, longitude: Double) = getClientClient.allNameRadius(wordCond,radius,latitude,longitude)

    //전체 반경
    suspend fun allRadius(radius: Double, latitude: Double, longitude: Double) = getClientClient.allRadius(radius,latitude,longitude)

    //특정 그룹 반경 - 이름
    suspend fun groupNameRadius(groupId : Int, wordCond : String, radius: Double, latitude: Double, longitude: Double) = getClientClient.groupNameRadius(groupId,wordCond,radius,latitude,longitude)

    //특정 그룹 반경
    suspend fun groupRadius(groupId : Int, radius: Double, latitude: Double, longitude: Double) = getClientClient.groupRadius(groupId,radius,latitude,longitude)

    // 그룹 조회
    suspend fun checkGroup() = getClientClient.checkGroup()

}