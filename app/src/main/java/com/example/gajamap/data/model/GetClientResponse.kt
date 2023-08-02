package com.example.gajamap.data.model

//특정 그룹내에 특정 고객 조회
data class GetGroupClientResponse(
    val address: Address,
    val clientId: Int,
    val clientName: String,
    val distance: Int,
    val groupInfo: GroupInfo,
    val image: Image,
    val location: Location,
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
    val clients: List<Client>
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
    val imageUrlPrefix : String
)


data class GetGroupAllClientResponse(
    val clients: List<Client>
)

data class GetRadiusResponse(
    val clients: List<Client>
)