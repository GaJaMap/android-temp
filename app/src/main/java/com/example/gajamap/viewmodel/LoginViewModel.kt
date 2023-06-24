package com.example.gajamap.viewmodel

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.ui.view.KakaoResponse
import com.example.gajamap.ui.view.LoginActivity
import com.example.gajamap.ui.view.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient

class LoginViewModel(private val tmp: String): ViewModel() {

    val kakaoResponseLiveData: MutableLiveData<KakaoResponse> = MutableLiveData()

    fun kakaoLogin(){

        val kakaoResponse = KakaoResponse(null,null)
        val context = GajaMapApplication.instance.applicationContext
        val kakaoTalkAvailable = UserApiClient.instance.isKakaoTalkLoginAvailable(context)
        if(kakaoTalkAvailable){
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->

                    kakaoResponse.token = token
                    kakaoResponse.error = error

                    kakaoResponseLiveData.value = kakaoResponse


            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->

                    kakaoResponse.token = token
                    kakaoResponse.error = error

                    kakaoResponseLiveData.value = kakaoResponse

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