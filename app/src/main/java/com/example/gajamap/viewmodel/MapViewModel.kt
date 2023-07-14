package com.example.gajamap.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import com.example.gajamap.data.model.GroupListData
import com.example.gajamap.data.repository.GroupRepository
import com.example.gajamap.data.response.CheckGroupResponse
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.response.CreateGroupResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MapViewModel: ViewModel() {
    private val groupRepository = GroupRepository()

    // 그룹 생성
    // 값이 변경되는 경우 MutableLiveData로 선언한다.
    private val _createGroup = MutableLiveData<CreateGroupResponse>()
    val createGroup : LiveData<CreateGroupResponse>
        get() = _createGroup

    fun createGroup(createRequest: CreateGroupRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.createGroup(createRequest)
            Log.d("createGroup", "$response\n${response.code()}")
            if(response.isSuccessful){
                _createGroup.postValue(response.body())
                Log.d("createGroupSuccess", "${response.body()}")

            }else {
                Log.d("createGroupError", "createGroup : ${response.message()}")
            }
        }
    }

    // 그룹 조회
    private val _checkGroup = MutableLiveData<ArrayList<GroupListData>>()
    val checkGroup : LiveData<ArrayList<GroupListData>>
        get() = _checkGroup
    private var checkItems = ArrayList<GroupListData>()

    /*
    fun buttonClick(){
        Log.d("checkckehck", "가즈아")
        val user = GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), id = 1, name = "그룹 10", person = "5")
        checkItems.add(user)
        _checkGroup.value = checkItems
    }*/

    fun checkGroup(){
        viewModelScope.launch {
            val response = groupRepository.checkGroup()
            Log.d("checkGroup", "$response\n${response.code()}")
            if(response.isSuccessful){
                val data = response.body()
                checkItems.clear()
                Log.d("checkGroupSuccess", "${response.body()}")
                val num = data!!.groupInfos.count()
                for (i in 0..num-1) {
                    val itemdata = data.groupInfos.get(i)
                    checkItems.add(GroupListData(img = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), id = itemdata.groupId, name = itemdata.groupName, person = itemdata.clientCount.toString()))
                }
                _checkGroup.value = checkItems
            }else {
                Log.d("checkGroupError", "checkGroup : ${response.message()}")
            }
        }
    }

    // 그룹 삭제
    private val _deleteGroup = MutableLiveData<CreateGroupResponse>()
    val deleteGroup : LiveData<CreateGroupResponse>
        get() = _deleteGroup

    fun deleteGroup(groupId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val response = groupRepository.deleteGroup(groupId)
            Log.d("deleteGroup", "$response\n${response.code()}")
            if(response.isSuccessful){
                Log.d("deleteGroupSuccess", "${response.body()}")
            }else {
                Log.d("deleteGroupError", "deleteGroup : ${response.message()}")
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