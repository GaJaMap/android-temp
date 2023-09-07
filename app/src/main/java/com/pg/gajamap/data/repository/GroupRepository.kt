package com.pg.gajamap.data.repository

import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.data.response.CreateGroupRequest
import com.pg.gajamap.data.service.GroupInterface

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

    // 특정 그룹내에 고객 전부 조회
    suspend fun getGroupAllClient(groupId : Long) = group.getGroupAllClient(groupId)

    // 특정 그룹 내 고객 검색 -> 조회할 고객 이름 검색
    suspend fun getGroupAllClientName(groupId : Long , wordCond : String) = group.getGroupAllClientName(groupId, wordCond)
}