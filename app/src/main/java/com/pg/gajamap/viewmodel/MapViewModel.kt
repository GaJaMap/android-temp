package com.pg.gajamap.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import com.pg.gajamap.data.model.*
import com.pg.gajamap.data.repository.GetClientRepository
import com.pg.gajamap.data.repository.GroupRepository
import com.pg.gajamap.data.repository.RadiusRepository
import com.pg.gajamap.data.response.CreateGroupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MapViewModel: ViewModel() {
    private val groupRepository = GroupRepository()
    private val radiusRepository = RadiusRepository()
    private val getClientRepository = GetClientRepository()

    // 값이 변경되는 경우 MutableLiveData로 선언한다.
    private val _checkGroup = MutableLiveData<ArrayList<GroupListData>>()
    val checkGroup : LiveData<ArrayList<GroupListData>>
        get() = _checkGroup
    private var checkItems = ArrayList<GroupListData>()

    // 그룹 생성
    fun createGroup(createRequest: CreateGroupRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.createGroup(createRequest)
            Log.d("createGroup", "$response\n${response.code()}")
            Log.d("createResponse", response.body().toString())
            if(response.isSuccessful){
                val data = response.body()
                // MapFragment에서 observer가 실행되기 위해서는 postValue가 필요하다!
                checkItems.add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), id = data!!, name = createRequest.name, person = "0", false, false))
                _checkGroup.postValue(checkItems)
                Log.d("createGroupSuccess", "${response.body()}")
            }else {
                Log.d("createGroupError", "createGroup : ${response.message()}")
            }
        }
    }

    // 그룹 조회
    fun checkGroup(){
        viewModelScope.launch {
            val response = groupRepository.checkGroup()
            Log.d("checkGroup", "$response\n${response.code()}")
            if(response.isSuccessful){
                val data = response.body()
                checkItems.clear()
                Log.d("checkGroupSuccess", "${response.body()}")
                val num = data!!.groupInfos.count()
                var count = 0
                checkItems.add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), id = 0, name = "전체", person = "0", true, true))
                for (i in 0..num-1) {
                    val itemdata = data.groupInfos.get(i)
                    count += itemdata.clientCount
                    checkItems.add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), id = itemdata.groupId, name = itemdata.groupName, person = itemdata.clientCount.toString(), false, false))
                }
                checkItems[0].person = count.toString()
                _checkGroup.value = checkItems
            }else {
                Log.d("checkGroupError", "checkGroup : ${response.message()}")
            }
        }
    }

    // 그룹 삭제
    fun deleteGroup(groupId: Long, pos: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.deleteGroup(groupId)
            Log.d("deleteGroup", "$response\n${response.code()}")
            if(response.isSuccessful){
                checkItems.removeAt(pos)
                _checkGroup.postValue(checkItems)
                Log.d("deleteGroupSuccess", "${response.body()}")
            }else {
                Log.d("deleteGroupError", "deleteGroup : ${response.message()}")
            }
        }
    }

    // 그룹 수정
    fun modifyGroup(groupId: Long, createRequest: CreateGroupRequest, pos: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.modifyGroup(groupId, createRequest)
            Log.d("modifyGroup", "$response\n${response.code()}")
            if(response.isSuccessful){
                checkItems.get(pos).name = createRequest.name
                _checkGroup.postValue(checkItems)
                Log.d("modifyGroupSuccess", "${response.body()}")

            }else {
                Log.d("modifyGroupError", "modifyGroup : ${response.message()}")
            }
        }
    }

    // 전체 고객 대상 반경 검색
    private val _wholeRadius = MutableLiveData<RadiusResponse>()
    val wholeRadius : LiveData<RadiusResponse>
        get() = _wholeRadius

    fun wholeRadius(radius: Int, latitude: Double, longitude: Double){
        viewModelScope.launch(Dispatchers.IO) {
            val response = radiusRepository.wholeRadius(radius, latitude, longitude)
            Log.d("wholeRadius", "$response\n${response.code()}")
            if(response.isSuccessful || response.code() == 422){
                // Livedata의 값을 변경해주는 함수 postValue()
                // setValue()와 다른점은 백그라운드에서 값을 변경해준다는 것, 백그라운드 쓰레드에서 동작하다가 메인 쓰레드에 값을 post 하는 방식으로 사용
                _wholeRadius.postValue(response.body())
                Log.d("wholeRadiusSuccess", "${response.body()}")

            }else {
                Log.d("wholeRadiusError", "wholeRadius : ${response.message()}")
            }
        }
    }

    // 특정 그룹 내 고객 대상 반경 검색
    fun specificRadius(radius: Int, latitude: Double, longitude: Double, groupId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = radiusRepository.specificRadius(groupId, radius, latitude, longitude)
            Log.d("specificRadius", "$response\n${response.code()}")
            if(response.isSuccessful || response.code() == 422){
                _wholeRadius.postValue(response.body())
                Log.d("specificRadiusSuccess", "${response.body()}")

            }else {
                Log.d("specificRadiusError", "specificRadius : ${response.message()}")
            }
        }
    }

    // 특정 그룹 내 고객 전부 조회
    private val _groupClients = MutableLiveData<GetAllClientResponse>()
    val groupClients : LiveData<GetAllClientResponse>
        get() = _groupClients

    fun getGroupAllClient(groupId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.getGroupAllClient(groupId)
            Log.d("getGroupAllClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _groupClients.postValue(response.body())
                Log.d("getGroupAllClientSuccess", "${response.body()}")
            }else {
                Log.d("getGroupAllClientError", "getGroupAllClient : ${response.message()}")
            }
        }
    }

    // 전체 고객 전부 조회
    private val _allClients = MutableLiveData<GetAllClientResponse>()
    val allClients : LiveData<GetAllClientResponse>
        get() = _allClients

    fun getAllClient(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getAllClient()
            Log.d("getAllClient", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _allClients.postValue(response.body())
                Log.d("getAllClientSuccess", "${response.body()}")
            }else {
                Log.d("getAllClientError", "getAllClient : ${response.message()}")
            }
        }
    }

    // 전체 고객 검색 -> 조회할 고객 이름 검색
    private val _allClientsName = MutableLiveData<GetAllClientResponse>()
    val allClientsName : LiveData<GetAllClientResponse>
        get() = _allClientsName

    fun getAllClientName(wordCond : String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = getClientRepository.getAllClientName(wordCond)
            Log.d("getAllClientName", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _allClientsName.postValue(response.body())
                Log.d("getAllClientNameSuccess", "${response.body()}")
            }else {
                Log.d("getAllClientNameError", "getAllClientName : ${response.message()}")
            }
        }
    }

    // 특정 그룹 내 고객 검색 -> 조회할 고객 이름 검색
    private val _groupClientsName = MutableLiveData<GetAllClientResponse>()
    val groupClientsName : LiveData<GetAllClientResponse>
        get() = _groupClientsName

    fun getGroupAllClientName(wordCond : String, groupId : Long){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.getGroupAllClientName(groupId, wordCond)
            Log.d("getGroupAllClientName", "${response.body()}\n${response.code()}")
            if(response.isSuccessful){
                _groupClientsName.postValue(response.body())
                Log.d("getGroupAllClientNameSuccess", "${response.body()}")
            }else {
                Log.d("getGroupAllClientNameError", "getGroupAllClientName : ${response.message()}")
            }
        }
    }

   // ViewModelFactory는 생성자 매개 변수를 사용하거나 사용하지 않고 ViewModel 개체를 인스턴스화함
    // ViewModel을 통해 전달되는 인자가 있을 때 사용
    class MapViewModelFactory()
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // modelClass에 MapViewModel이 상속되었는지 확인
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                // 맞다면 MapViewModel의 파라미터 값을 넘겨줌
                return MapViewModel() as T
            }
            // 상속이 되지 않았다면 IllegalArgumentException을 통해 상속이 되지 않았다는 에러를 띄움
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}