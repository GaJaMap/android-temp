package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.service.GroupInterface

class GroupRepository {
    companion object{
        val group = GajaMapApplication.sRetrofit.create(GroupInterface::class.java)
    }
    private val group = GajaMapApplication.sRetrofit.create(GroupInterface::class.java)

    // 그룹 생성
    suspend fun createGroup(createGroupRequest: CreateGroupRequest) = group.createGroup(createGroupRequest)
    // 그룹 조회
    //suspend fun checkGroup() = group.checkGroup()
}