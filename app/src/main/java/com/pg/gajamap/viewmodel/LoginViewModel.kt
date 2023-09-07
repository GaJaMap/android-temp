package com.pg.gajamap.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.data.model.AutoLoginResponse
import com.pg.gajamap.data.model.LoginRequest
import com.pg.gajamap.data.model.LoginResponse
import com.pg.gajamap.data.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val tmp: String): ViewModel() {

    private val loginRepository = LoginRepository()

    private val _login = MutableLiveData<LoginResponse>()
    val login : LiveData<LoginResponse>
        get() = _login

    private val _autoLogin = MutableLiveData<AutoLoginResponse>()
    val autoLogin : LiveData<AutoLoginResponse>
    get() = _autoLogin


    fun autoLogin(){
        viewModelScope.launch  (Dispatchers.IO){
            val response = loginRepository.autoLogin()
            Log.d("autoLogin", "${response}\n${response.code()}")
            //response.body()?.let { handleAutoLoginResponse(response.code(), it) }
            if(response.isSuccessful){
                _autoLogin.postValue(response.body())
                Log.d("autologinSuccess", "${response.body()}")
            }else {
                Log.d("autologinError", "autologin : ${response.message()}")
            }
        }
    }

    fun autoLoginKakao(){
        viewModelScope.launch  (Dispatchers.IO){
            val response = loginRepository.autoLogin()
            Log.d("autoLogin", "${response}\n${response.code()}")
            _autoLogin.postValue(response.body())
        }
    }
    fun postLogin(loginRequest: LoginRequest){
        viewModelScope.launch  (Dispatchers.IO) {
            val response = loginRepository.postLogin(loginRequest)
            Log.d("postLogin", "${response}\n${response.code()}")
            if(response.isSuccessful){
                autoLogin()
                _login.postValue(response.body())
                val header = response.headers()
                val contentType = header["Set-Cookie"]?.split(";")?.get(0)
                //val session = header["Set-Cookie"]?.split(";")?.get(0)
                val session = contentType?.replace("SESSION=","")
                //val session = contentType?.replace("","")
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