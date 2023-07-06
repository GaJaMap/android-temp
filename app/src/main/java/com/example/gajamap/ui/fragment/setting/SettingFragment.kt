package com.example.gajamap.ui.fragment.setting

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentSettingBinding
import com.example.gajamap.viewmodel.AddViewModel
import com.example.gajamap.viewmodel.SettingViewModel

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    override val viewModel by viewModels<SettingViewModel> {
        SettingViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@SettingFragment
        binding.fragment = this@SettingFragment
    }

    override fun onCreateAction() {

        //문의하기
        binding.settingInquireTv.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, InquireFragment()).addToBackStack(null).commit()
        }

        //엑셀 파일 업로드
        binding.settingExcelTv.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, ExcelFragment()).addToBackStack(null).commit()
        }

        //연락처 업로드
        binding.settingPhoneTv.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, PhoneFragment()).addToBackStack(null).commit()
        }

        //카카오 프로필
        binding.settingKakaoTv.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.nav_fl, KakaoProfileFragment()).addToBackStack(null).commit()
        }

    }
}