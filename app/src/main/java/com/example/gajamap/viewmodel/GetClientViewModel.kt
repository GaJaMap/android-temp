package com.example.gajamap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.gajamap.data.model.GetAllClientResponse
import com.example.gajamap.data.model.GetGroupAllClientResponse
import com.example.gajamap.data.model.GetGroupClientResponse
import com.example.gajamap.data.repository.GetClientRepository
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


    fun getGroupClient(groupId : Int, client : Int){
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

    fun getGroupAllClient(wordCond : String, groupId : Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getGroupAllClient(wordCond, groupId)
            Log.d("getGroupAllClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _getGroupAllClient.postValue(response.body())
                Log.d("getGroupAllClientSuccess", "${response.body()}")
            }else {
                Log.d("getGroupAllClientError", "getGroupAllClient : ${response.message()}")
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