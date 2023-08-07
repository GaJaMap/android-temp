package com.example.gajamap.data.repository

import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.service.GroupInterface

class GroupRepository {
    private val group = GajaMapApplication.sRetrofit.create(GroupInterface::class.java)

    // 그룹 생성
    suspend fun createGroup(createGroupRequest: CreateGroupRequest) = group.createGroup(createGroupRequest)
    // 그룹 조회
    suspend fun checkGroup() = group.checkGroup()
    // 그룹 삭제
    suspend fun deleteGroup(groupId : Long) = group.deleteGroup(groupId)
    // 그룹 수정
    suspend fun modifyGroup(groupId : Long, createGroupRequest: CreateGroupRequest) = group.modifyGroup(groupId, createGroupRequest)

    //특정 그룹내에 고객 전부 조회
    suspend fun getGroupAllClient(groupId : Long) = group.getGroupAllClient(groupId)
}