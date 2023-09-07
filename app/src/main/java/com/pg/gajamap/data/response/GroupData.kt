package com.pg.gajamap.data.response

import com.google.gson.annotations.SerializedName

data class CreateGroupRequest(
    val name : String = ""
)
data class GroupResponse(
    val message : String = ""
)

data class CheckGroupResponse(
    @SerializedName("hasNext")
    val hasNext : Boolean,
    @SerializedName("groupInfos")
    var groupInfos: List<GroupInfo> = arrayListOf()
)
data class GroupInfo(
    @SerializedName("groupId")
    val groupId: Long,
    @SerializedName("clientCount")
    val clientCount: Int,
    @SerializedName("groupName")
    val groupName: String
)
