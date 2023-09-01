package com.example.gajamap.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.base.GajaMapApplication
import com.example.gajamap.base.UserData
import com.example.gajamap.data.model.Address
import com.example.gajamap.data.model.AutoLoginResponse
import com.example.gajamap.data.model.LoginRequest
import com.example.gajamap.data.model.LoginResponse
import com.example.gajamap.data.response.SearchResultData
import com.example.gajamap.databinding.ActivityLoginBinding
import com.example.gajamap.databinding.ActivityMainBinding
import com.example.gajamap.ui.adapter.SearchResultAdapter
import com.example.gajamap.viewmodel.LoginViewModel
import com.example.gajamap.viewmodel.MainViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    // SearchResult recyclerview
    private val searchResultList = arrayListOf<SearchResultData>()
    private val searchResultAdapter = SearchResultAdapter(searchResultList)

    override val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.LoginViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@LoginActivity
        binding.activity = this@LoginActivity
    }

    override fun onCreateAction() {

        viewModel.autoLogin()
        viewModel.autoLogin.observe(this, Observer {
            // 싱글톤 패턴을 이용하여 자동 로그인 response 데이터 값 저장
            UserData.clientListResponse = it.clientListResponse
            UserData.groupinfo = it.groupInfo
//            GajaMapApplication.prefs.saveAutoLoginResponse(it)
            GajaMapApplication.prefs.setString("imageUrlPrefix", it.imageUrlPrefix)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
    }

    fun kakaoLogin(){
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("kakao", "카카오계정으로 로그인 실패 ${error}")
            } else if (token != null) {
                Log.d("kakoAccessToken", token.accessToken)
                //Log.d("kakoRefreshToken", token.refreshToken)
                postLogin(token.accessToken)
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.d("kakao", "카카오톡으로 로그인 실패 ${error}")
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.d("kakoAccessToken", token.accessToken)
                    postLogin(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    //로그인 api
    private fun postLogin(token: String){

        Log.d("kakoAccessToken_1", token)

        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {  // 비동기 작업 시작
                viewModel.postLogin(LoginRequest(token))// postLogin 호출 및 결과 대기
                //viewModel.autoLogin()
            }

        }

        viewModel.login.observe(this, Observer { it ->
            viewModel.autoLogin.observe(this@LoginActivity, Observer {
                GajaMapApplication.prefs.saveAutoLoginResponse(it)
                // autoLogin이 완료된 후에 MainActivity로 이동합니다.
                    GajaMapApplication.prefs.setString("imageUrlPrefix", it.imageUrlPrefix)

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            })
            GajaMapApplication.prefs.setString("authority", it.authority.toString())
        })
    }

}