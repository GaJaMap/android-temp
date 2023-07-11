package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.service.ApiInterface

class GetClientRepository {

    private val getClientClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //특정 그룹내에 특정 고객 조회
    suspend fun getGroupClient(groupId : Int, client : Int) = getClientClient.getGroupClient(groupId, client)

    //전체 고객 검색
    suspend fun getAllClient() = getClientClient.getAllClient()

    //전체 고객 검색 -> 조회할 고객 이름 검색
    suspend fun getAllClientName(wordCond : String) = getClientClient.getAllClientName(wordCond)

    //특정 그룹내에 고객 전부 조회
    suspend fun getGroupAllClient(wordCond : String, groupId : Int) = getClientClient.getGroupAllClient(wordCond, groupId)
}