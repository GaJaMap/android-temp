package com.pg.gajamap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel(private val tmp: String): ViewModel() {
    //LiveData
    // 값이 변경되는 경우 MutableLiveData로 선언한다.


    // ViewModelFactory는 생성자 매개 변수를 사용하거나 사용하지 않고 ViewModel 개체를 인스턴스화함
    // ViewModel을 통해 전달되는 인자가 있을 때 사용
    class MainViewModelFactory(private val tmp: String)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // modelClass에 MainViewModel이 상속되었는지 확인
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                // 맞다면 MainViewModel의 파라미터 값을 넘겨줌
                return MainViewModel(tmp) as T
            }
            // 상속이 되지 않았다면 IllegalArgumentException을 통해 상속이 되지 않았다는 에러를 띄움
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}