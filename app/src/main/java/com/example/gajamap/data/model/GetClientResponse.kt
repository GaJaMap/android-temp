package com.example.gajamap.data.model

import com.google.gson.annotations.SerializedName

//특정 그룹내에 특정 고객 조회
data class GetGroupClientResponse(
    val address: Address,
    val clientId: Int,
    val clientName: String,
    val distance: Int,
    val groupInfo: GroupInfo,
    val image: Image,
    val location: Location,
    val createdAt : String,
    val phoneNumber: String
)

data class GroupInfo(
    val groupId: Int,
    val groupName: String
)

data class Image(
    val filePath: String,
    val originalFileName: String
)

data class GetAllClientResponse(
    val clients: List<Client>,
    val imageUrlPrefix : String
)

data class ClientOne(
    val address: Address,
    val clientId: Int,
    val clientName: String,
    val distance: Int,
    val groupInfo: GroupInfo,
    val image: Image,
    val location: Location,
    val phoneNumber: String
)

data class Client(
    val address: Address,
    val clientId: Int,
    val clientName: String,
    val distance: Int,
    val groupInfo: GroupInfo,
    val image: Image,
    val location: Location,
    val phoneNumber: String,
    val createdAt : String,
    val imageUrlPrefix : String
)


data class GetGroupAllClientResponse(
    val clients: List<Client>,
    val imageUrlPrefix : String
)

data class GetRadiusResponse(
    val clients: List<Client>,
    val imageUrlPrefix : String
)

data class GroupResponse(
    val hasNext : Boolean,
    var groupInfos: List<GroupInfoResponse>
)
data class GroupInfoResponse(
    val groupId: Int,
    val clientCount: Int,
    val groupName: String
)