package com.example.gajamap.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gajamap.BuildConfig
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.DeleteRequest
import com.example.gajamap.data.model.KakaoMapImage
import com.example.gajamap.data.service.ApiInterface
import com.example.gajamap.data.service.ImgKakaoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetClientRepository {

    private val getClientClient = GajaMapApplication.sRetrofit.create(ApiInterface::class.java)

    //특정 그룹내에 특정 고객 조회
    suspend fun getGroupClient(groupId : Int, client : Int) = getClientClient.getGroupClient(groupId, client)

    //전체 고객 검색
    suspend fun getAllClient() = getClientClient.getAllClient()

    //전체 고객 검색 -> 조회할 고객 이름 검색
    suspend fun getAllClientName(wordCond : String) = getClientClient.getAllClientName(wordCond)

    //특정 그룹내에 고객 전부 조회 -> 이름 검색
    suspend fun getGroupAllClientName(groupId : Int , wordCond : String) = getClientClient.getGroupAllClientName(groupId, wordCond)

    //특정 그룹내에 고객 전부 조회
    suspend fun getGroupAllClient(groupId : Int) = getClientClient.getGroupAllClient(groupId)

    //고객 삭제
    suspend fun deleteClient(groupId : Int, client : Int) = getClientClient.deleteClient(groupId, client)

    suspend fun deleteAnyClient(groupId : Int, deleteRequest: DeleteRequest) = getClientClient.deleteAnyClient(groupId, deleteRequest)

    //전체 반경 - 이름
    suspend fun allNameRadius(wordCond : String, radius: Double, latitude: Double, longitude: Double) = getClientClient.allNameRadius(wordCond,radius,latitude,longitude)

    //전체 반경
    suspend fun allRadius(radius: Double, latitude: Double, longitude: Double) = getClientClient.allRadius(radius,latitude,longitude)

    //특정 그룹 반경 - 이름
    suspend fun groupNameRadius(groupId : Int, wordCond : String, radius: Double, latitude: Double, longitude: Double) = getClientClient.groupNameRadius(groupId,wordCond,radius,latitude,longitude)

    //특정 그룹 반경
    suspend fun groupRadius(groupId : Int, radius: Double, latitude: Double, longitude: Double) = getClientClient.groupRadius(groupId,radius,latitude,longitude)

    // 그룹 조회
    suspend fun checkGroup() = getClientClient.checkGroup()


    private val BASE_URL = "https://dapi.kakao.com/"
    private val API_KEY = BuildConfig.KAKAO_API_KEY
    fun getMapImage(center: String, width: Int, height: Int, level: Int): LiveData<String> {
        val result = MutableLiveData<String>()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val mapService = retrofit.create(ImgKakaoService::class.java)

        val call = mapService.getMapImage(center, width, height, level, API_KEY)

        call.enqueue(object : Callback<KakaoMapImage> {
            override fun onResponse(call: Call<KakaoMapImage>, response: Response<KakaoMapImage>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.imageUrl
                    imageUrl?.let {
                        result.postValue(it)
                    }
                }
            }

            override fun onFailure(call: Call<KakaoMapImage>, t: Throwable) {
                t.printStackTrace()
            }
        })

        return result
    }

}