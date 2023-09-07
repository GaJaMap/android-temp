package com.pg.gajamap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.pg.gajamap.data.model.*
import com.pg.gajamap.data.repository.GetClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetClientViewModel(private val tmp: String): ViewModel() {

    private val getClientRepository = GetClientRepository()

    private val _getGroupClient = MutableLiveData<GetGroupClientResponse>()
    val getGroupClient : LiveData<GetGroupClientResponse>
    get() = _getGroupClient

    private val _getAllClient = MutableLiveData<GetAllClientResponse>()
    val getAllClient : LiveData<GetAllClientResponse>
    get() = _getAllClient

    private val _getAllClientName = MutableLiveData<GetAllClientResponse>()
    val getAllClientName : LiveData<GetAllClientResponse>
    get() = _getAllClientName

    private val _getGroupAllClient = MutableLiveData<GetGroupAllClientResponse>()
    val getGroupAllClient : LiveData<GetGroupAllClientResponse>
    get() = _getGroupAllClient

    private val _getGroupAllClientName = MutableLiveData<GetGroupAllClientResponse>()
    val getGroupAllClientName : LiveData<GetGroupAllClientResponse>
        get() = _getGroupAllClientName


    private val _deleteClient = MutableLiveData<BaseResponse>()
    val deleteClient : LiveData<BaseResponse>
        get() = _deleteClient

    private val _deleteAnyClient = MutableLiveData<BaseResponse>()
    val deleteAnyClient : LiveData<BaseResponse>
        get() = _deleteAnyClient

    private val _allNameRadius = MutableLiveData<GetRadiusResponse>()
    val allNameRadius : LiveData<GetRadiusResponse>
    get() = _allNameRadius

    private val _allRadius = MutableLiveData<GetRadiusResponse>()
    val allRadius : LiveData<GetRadiusResponse>
    get() = _allRadius

    private val _groupNameRadius = MutableLiveData<GetRadiusResponse>()
    val groupNameRadius : LiveData<GetRadiusResponse>
    get() = _groupNameRadius

    private val _groupRadius = MutableLiveData<GetRadiusResponse>()
    val groupRadius : LiveData<GetRadiusResponse>
    get() = _groupRadius

    private val _checkGroup = MutableLiveData<GroupResponse>()
    val checkGroup : LiveData<GroupResponse>
    get() = _checkGroup


    //그룹 조회
    fun checkGroup(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.checkGroup()
            Log.d("checkGroup", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _checkGroup.postValue(response.body())
                Log.d("checkGroupSuccess", "${response.body()}")
            }else {
                Log.d("checkGroupError", "checkGroup : ${response.message()}")
            }
        }
    }
    fun deleteClient(groupId : Long, client : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.deleteClient(groupId, client)
            Log.d("deleteClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _deleteClient.postValue(response.body())
                Log.d("deleteClientSuccess", "${response.body()}")
            }else {
                Log.d("deleteClientError", "deleteClient : ${response.message()}")
            }
        }
    }

    fun deleteAnyClient(groupId : Long, deleteRequest: DeleteRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.deleteAnyClient(groupId, deleteRequest)
            Log.d("deleteAnyClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _deleteAnyClient.postValue(response.body())
                Log.d("deleteAnyClientSuccess", "${response.body()}")
            }else {
                Log.d("deleteAnyClientError", "deleteAnyClient : ${response.message()}")
            }
        }
    }

    fun getGroupClient(groupId : Long, client : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getGroupClient(groupId, client)
            Log.d("getGroupClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _getGroupClient.postValue(response.body())
                Log.d("getGroupClientSuccess", "${response.body()}")
            }else {
                Log.d("getGroupClientError", "getGroupClient : ${response.message()}")
            }
        }
    }

    fun getAllClient(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getAllClient()
            Log.d("getAllClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _getAllClient.postValue(response.body())
                Log.d("getAllClientSuccess", "${response.body()}")
            }else {
                Log.d("getAllClientError", "getAllClient : ${response.message()}")
            }
        }
    }

    fun getAllClientName(wordCond : String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getAllClientName(wordCond)
            Log.d("getAllClientName", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _getAllClientName.postValue(response.body())
                Log.d("getAllClientNameSuccess", "${response.body()}")
            }else {
                Log.d("getAllClientNameError", "getAllClientName : ${response.message()}")
            }
        }
    }

    fun getGroupAllClientName(wordCond : String, groupId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getGroupAllClientName(groupId, wordCond)
            Log.d("getGroupAllClientName", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _getGroupAllClientName.postValue(response.body())
                Log.d("getGroupAllClientNameSuccess", "${response.body()}")
            }else {
                Log.d("getGroupAllClientNameError", "getGroupAllClientName : ${response.message()}")
            }
        }
    }

    fun getGroupAllClient(groupId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getGroupAllClient(groupId)
            Log.d("getGroupAllClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _getGroupAllClient.postValue(response.body())
                Log.d("getGroupAllClientSuccess", "${response.body()}")
            }else {
                Log.d("getGroupAllClientError", "getGroupAllClient : ${response.message()}")
            }
        }
    }

    //전체 반경 - 이름
    fun allNameRadius(wordCond : String, radius: Double, latitude: Double, longitude: Double){
        viewModelScope.launch(Dispatchers.IO){
            val response = getClientRepository.allNameRadius(wordCond,radius,latitude,longitude)
            Log.d("allNameRadius", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _allNameRadius.postValue(response.body())
                Log.d("allNameRadiusSuccess", "${response.body()}")
            }else {
                Log.d("allNameRadiusError", "allNameRadius : ${response.message()}")
            }
        }
    }

    //전체 반경
    fun allRadius(radius: Double, latitude: Double, longitude: Double){
        viewModelScope.launch(Dispatchers.IO){
            val response = getClientRepository.allRadius(radius,latitude,longitude)
            Log.d("allRadius", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _allRadius.postValue(response.body())
                Log.d("allRadiusSuccess", "${response.body()}")
            }else {
                Log.d("allRadiusError", "allRadius : ${response.message()}")
            }
        }
    }

    //특정 그룹 반경 - 이름
    fun groupNameRadius(groupId : Long, wordCond : String, radius: Double, latitude: Double, longitude: Double){
        viewModelScope.launch(Dispatchers.IO){
            val response = getClientRepository.groupNameRadius(groupId,wordCond,radius,latitude,longitude)
            Log.d("groupNameRadius", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _groupNameRadius.postValue(response.body())
                Log.d("groupNameRadiusSuccess", "${response.body()}")
            }else {
                Log.d("groupNameRadiusError", "groupNameRadius : ${response.message()}")
            }
        }
    }

    //특정 그룹 반경
    fun groupRadius(groupId : Long, radius: Double, latitude: Double, longitude: Double){
        viewModelScope.launch(Dispatchers.IO){
            val response = getClientRepository.groupRadius(groupId,radius,latitude,longitude)
            Log.d("groupRadius", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _groupRadius.postValue(response.body())
                Log.d("groupRadiusSuccess", "${response.body()}")
            }else {
                Log.d("groupRadiusError", "groupRadius : ${response.message()}")
            }
        }
    }

    class AddViewModelFactory(private val tmp: String)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // modelClass에 MainViewModel이 상속되었는지 확인
            if (modelClass.isAssignableFrom(GetClientViewModel::class.java)) {
                // 맞다면 MainViewModel의 파라미터 값을 넘겨줌
                return GetClientViewModel(tmp) as T
            }
            // 상속이 되지 않았다면 IllegalArgumentException을 통해 상속이 되지 않았다는 에러를 띄움
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}