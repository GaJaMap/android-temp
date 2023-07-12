package com.example.gajamap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.gajamap.data.repository.GroupRepository
import com.example.gajamap.data.response.CreateGroupRequest
import com.example.gajamap.data.response.CreateGroupResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(private val tmp: String): ViewModel() {
    private val groupRepository = GroupRepository()

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


    // ViewModelFactory는 생성자 매개 변수를 사용하거나 사용하지 않고 ViewModel 개체를 인스턴스화함
    // ViewModel을 통해 전달되는 인자가 있을 때 사용
    class MapViewModelFactory(private val tmp: String)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // modelClass에 MapViewModel이 상속되었는지 확인
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                // 맞다면 MapViewModel의 파라미터 값을 넘겨줌
                return MapViewModel(tmp) as T
            }
            // 상속이 되지 않았다면 IllegalArgumentException을 통해 상속이 되지 않았다는 에러를 띄움
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}