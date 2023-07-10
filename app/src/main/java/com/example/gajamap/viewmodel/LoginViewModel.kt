package com.example.gajamap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.data.model.LoginRequest
import com.example.gajamap.data.model.LoginResponse
import com.example.gajamap.data.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val tmp: String): ViewModel() {

    private val loginRepository = LoginRepository()

    //var login = MutableLiveData<Boolean>(true)
    private val _login = MutableLiveData<LoginResponse>()
    val login : LiveData<LoginResponse>
        get() = _login

    fun postLogin(loginRequest: LoginRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val response = loginRepository.postLogin(loginRequest)
            Log.d("postLogin", "$response\n${response.code()}")
            if(response.isSuccessful){
                _login.postValue(response.body())
                val header = response.headers()
                val contentType = header["Set-Cookie"]?.split(";")?.get(0)
                val session = contentType?.replace("SESSION=","")
                Log.d("session", "$session")
                if (session != null) {
                    GajaMapApplication.prefs.setString("session", session)
                }

                Log.d("postSuccess", "${response.body()}")
            }else {
                Log.d("loginError", "postLogin : ${response.message()}")
            }
        }
    }

    class LoginViewModelFactory(private val tmp: String)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // modelClass에 MainViewModel이 상속되었는지 확인
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                // 맞다면 MainViewModel의 파라미터 값을 넘겨줌
                return LoginViewModel(tmp) as T
            }
            // 상속이 되지 않았다면 IllegalArgumentException을 통해 상속이 되지 않았다는 에러를 띄움
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}