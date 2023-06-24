package com.example.gajamap.ui.view

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.R
import com.example.gajamap.base.BaseActivity
import com.example.gajamap.databinding.ActivityMainBinding
import com.example.gajamap.viewmodel.MainViewModel
import com.kakao.sdk.common.util.Utility

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override val viewModel by viewModels<MainViewModel> {
        MainViewModel.MainViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.lifecycleOwner = this@MainActivity
        binding.viewModel = this.viewModel
    }

    override fun onCreateAction() {
        // 이곳에 화면 기능 구현
        //카카오 해시키
        //Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")
    }
}