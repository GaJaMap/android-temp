package com.example.gajamap.data.model

import com.google.gson.annotations.SerializedName

data class RadiusResponse(
    @SerializedName("clients")
    var clients: List<RadiusClient> = arrayListOf()
)
data class RadiusClient(
    @SerializedName("clientId")
    val clientId: Int,
    @SerializedName("groupInfo")
    val groupInfo: GroupInfoData?,
    @SerializedName("clientName")
    val clientName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("address")
    val address: AddressData?,
    @SerializedName("location")
    val location: LocationData?,
    @SerializedName("image")
    val image: ImageData?,
    @SerializedName("distance")
    val distance: Double
)
data class GroupInfoData(
    @SerializedName("groupId")
    val groupId: Int,
    @SerializedName("groupName")
    val groupName: String
)
data class AddressData(
    @SerializedName("province")
    val province: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("district")
    val district: String,
    @SerializedName("detail")
    val detail: String
)
data class LocationData(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
data class ImageData(
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("originalFileName")
    val originalFileName: String
)