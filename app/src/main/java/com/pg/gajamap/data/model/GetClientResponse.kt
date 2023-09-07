package com.pg.gajamap.data.model

data class AutoLoginResponse(
    var clientListResponse : GetAllClientResponse,
    var imageUrlPrefix : String,
    var groupInfo : AutoLoginGroupInfo
)

data class AutoLoginGroupInfo(
    var groupId : Long,
    var clientCount : Int,
    var groupName : String
)

//특정 그룹내에 특정 고객 조회
data class GetGroupClientResponse(
    var address: Address,
    var clientId: Long,
    var clientName: String,
    var distance: Double?=null,
    var groupInfo: GroupInfo,
    var image: Image,
    var location: Location,
    var createdAt : String,
    var phoneNumber: String
)

data class GroupInfo(
    var groupId: Long,
    var groupName: String
)

data class Image(
    var filePath: String?=null,
    var originalFileName: String?=null
)

data class GetAllClientResponse(
    var clients: MutableList<Client>,
    var imageUrlPrefix : String?=null
)

data class Client(
    var address: Address,
    var clientId: Long,
    var clientName: String,
    var distance: Double?=null,
    var groupInfo: GroupInfo,
    var image: Image,
    var location: Location,
    var phoneNumber: String,
    var createdAt : String,
)

data class GetGroupAllClientResponse(
    var clients: List<Client>,
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
    var groupId: Long,
    var clientCount: Int,
    var groupName: String
)