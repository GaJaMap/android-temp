package com.example.gajamap.data.model

import com.google.gson.annotations.SerializedName

data class AutoLoginResponse(
    var clientListResponse : GetAllClientResponse,
    var groupInfo : AutoLoginGroupInfo
)

data class AutoLoginGroupInfo(
    var groupId : Int,
    var clientCount : Int,
    var groupName : String
)
//특정 그룹내에 특정 고객 조회
data class GetGroupClientResponse(
    var address: Address,
    var clientId: Int,
    var clientName: String,
    var distance: Double?=null,
    var groupInfo: GroupInfo,
    var image: Image,
    var location: Location,
    var createdAt : String,
    var phoneNumber: String
)

data class GroupInfo(
    var groupId: Int,
    var groupName: String
)

data class Image(
    var filePath: String,
    var originalFileName: String
)

data class GetAllClientResponse(
    var clients: List<Client>,
    var imageUrlPrefix : String
)

data class ClientOne(
    var address: Address,
    var clientId: Int,
    var clientName: String,
    var distance: Double?=null,
    var groupInfo: GroupInfo,
    var image: Image,
    var location: Location,
    var phoneNumber: String
)

data class Client(
    var address: Address,
    var clientId: Int,
    var clientName: String,
    var distance: Double?=null,
    var groupInfo: GroupInfo,
    var image: Image,
    var location: Location,
    var phoneNumber: String,
    var createdAt : String,
    var imageUrlPrefix : String
)


data class GetGroupAllClientResponse(
    var clients: List<Client>,
    var imageUrlPrefix : String
)

data class GetRadiusResponse(
    var clients: List<Client>,
    var imageUrlPrefix : String
)

data class GroupResponse(
    var hasNext : Boolean,
    var groupInfos: List<GroupInfoResponse>
)
data class GroupInfoResponse(
    var groupId: Int,
    var clientCount: Int,
    var groupName: String
)