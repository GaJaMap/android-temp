package com.pg.gajamap.data.model

data class PostClientRequest(
    val address: Address,
    val clientImage: String,
    val clientName: String,
    val groupId: Int,
    val location: Location,
    val phoneNumber: String,
    val isBasicImage : Boolean
)

data class PostKakaoPhoneRequest(
    val clients: MutableList<Clients?>,
    val groupId: Int
)

data class Clients(
    val clientName: String,
    val phoneNumber: String
)

data class BaseResponse(
    val message : String = ""
)

data class PutClientRequest(
    val address: Address,
    val clientImage: String,
    val clientName: String,
    val groupId: Int,
    val location: Location,
    val phoneNumber: String,
    val isBasicImage : Boolean
)

data class DeleteRequest(
    val clientIds : List<Long>
)