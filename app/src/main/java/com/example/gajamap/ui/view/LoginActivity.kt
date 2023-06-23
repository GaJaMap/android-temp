package com.example.gajamap.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.databinding.ActivityLoginBinding
import com.example.gajamap.databinding.ActivityMainBinding
import com.example.gajamap.viewmodel.LoginViewModel
import com.example.gajamap.viewmodel.MainViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    override val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.LoginViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        //binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@LoginActivity
        binding.viewModel = this.viewModel
    }

    override fun onCreateAction() {
        //카카오 로그인
        val loginObserve = Observer<KakaoResponse>{ response ->
            if (response.error != null) {
                Log.d("kakao", "로그인 실패 ${response.error}")
            } else if (response.token != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        viewModel.kakaoResponseLiveData.observe(this, loginObserve)

    }


}