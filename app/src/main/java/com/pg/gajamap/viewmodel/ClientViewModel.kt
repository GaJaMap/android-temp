package com.pg.gajamap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.pg.gajamap.data.model.*
import com.pg.gajamap.data.repository.ClientRespository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ClientViewModel(private val tmp: String): ViewModel() {

    private val clientRepository = ClientRespository()


    private val _putClient = MutableLiveData<Client>()
    val putClient : LiveData<Client>
    get() = _putClient

    private val _deleteClient = MutableLiveData<BaseResponse>()
    val deleteClient : LiveData<BaseResponse>
    get() = _deleteClient

    private val _postClient = MutableLiveData<Response<Client>>()
    val postClient : LiveData<Response<Client>>

//    private val _postClient = MutableLiveData<Response<Int>>()
//    val postClient : LiveData<Response<Int>>
    get() = _postClient

    private val _postKakaoPhoneClient = MutableLiveData<Response<List<Int>>>()
    val postKakaoPhoneClient : LiveData<Response<List<Int>>>
    get() = _postKakaoPhoneClient

    private val _checkGroup = MutableLiveData<GroupResponse>()
    val checkGroup : LiveData<GroupResponse>
        get() = _checkGroup

    private val _logout = MutableLiveData<BaseResponse>()
    val logout : LiveData<BaseResponse>
    get() =  _logout

    private val _withdraw = MutableLiveData<BaseResponse>()
    val withdraw : LiveData<BaseResponse>
    get() = _withdraw


    //그룹 조회
    fun checkGroup(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.checkGroup()
            Log.d("checkGroup", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _checkGroup.postValue(response.body())
                Log.d("checkGroupSuccess", "${response.body()}")
            }else {
                Log.d("checkGroupError", "checkGroup : ${response.message()}")
            }
        }
    }

    fun putClient(groupId : Long, client : Long, clientName: RequestBody,
                  group : RequestBody,
                  phoneNumber : RequestBody,
                  mainAddress : RequestBody,
                  detail : RequestBody,
                  latitude : RequestBody,
                  longitude : RequestBody,
                  clientImage : MultipartBody.Part?,
                  isBasicImage : RequestBody) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.putClient(groupId, client, clientName,group,phoneNumber, mainAddress , detail, latitude, longitude, clientImage, isBasicImage)
            Log.d("putClient", "${response.body()}\n${response.code()}")
            //val clientResponse = response.body() as? Client
            if(response.isSuccessful){
                _putClient.postValue(response.body())
                Log.d("putClientSuccess", "${response.body()}")
            }else {
                Log.d("putClientError", "putClient : ${response.errorBody()}")
            }
        }
    }


    /*fun deleteClient(groupId : Int, client : Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.deleteClient(groupId, client)
            Log.d("deleteClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _deleteClient.postValue(response.body())
                Log.d("deleteClientSuccess", "${response.body()}")
            }else {
                Log.d("deleteClientError", "deleteClient : ${response.message()}")
            }
        }
    }*/

    fun postClient(clientName: RequestBody,
                   groupId : RequestBody,
                   phoneNumber : RequestBody,
                   mainAddress : RequestBody,
                   detail : RequestBody,
                   latitude : RequestBody,
                   longitude : RequestBody,
                   clientImage : MultipartBody.Part?,
                   isBasicImage : RequestBody){
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.postClient(clientName,groupId,phoneNumber, mainAddress , detail, latitude, longitude, clientImage, isBasicImage)
            Log.d("postClient", "${response}\n${response.code()}")
            if(response.isSuccessful){
                _postClient.postValue(response)
                Log.d("postClientSuccess", "$response")
            }else {
                Log.d("postClientError", "postClient : ${response.errorBody()?.string()}")
            }
        }
    }

    fun postKakaoPhoneClient(postKakaoPhoneRequest: PostKakaoPhoneRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.postKakaoPhoneClient(postKakaoPhoneRequest)
            Log.d("postKakaoPhoneClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _postKakaoPhoneClient.postValue(response)
                Log.d("postKakaoPhoneClientSuccess", "${response.body()}")
            }else {
                Log.d("postKakaoPhoneClientError", "postKakaoPhoneClient : ${response.message()}")
            }
        }
    }

    fun logout(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.logout()
            Log.d("logout", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _logout.postValue(response.body())
                Log.d("logoutSuccess", "${response.body()}")
            }else {
                Log.d("logoutError", "logout : ${response.message()}")
            }
        }
    }

    fun withdraw(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = clientRepository.withdraw()
            Log.d("withdraw", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _withdraw.postValue(response.body())
                Log.d("withdrawSuccess", "${response.body()}")
            }else {
                Log.d("withdrawError", "withdraw : ${response.message()}")
            }
        }
    }

    class SettingViewModelFactory(private val tmp: String)
        :ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // modelClass에 MainViewModel이 상속되었는지 확인
            if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
                // 맞다면 MainViewModel의 파라미터 값을 넘겨줌
                return ClientViewModel(tmp) as T
            }
            // 상속이 되지 않았다면 IllegalArgumentException을 통해 상속이 되지 않았다는 에러를 띄움
            throw IllegalArgumentException("Not found ViewModel class.")
        }
        }
}