package com.pg.gajamap.ui.fragment.setting

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.pg.gajamap.BR
import com.pg.gajamap.R
import com.pg.gajamap.base.BaseFragment
import com.pg.gajamap.base.GajaMapApplication
import com.pg.gajamap.databinding.FragmentSettingBinding
import com.pg.gajamap.ui.view.LoginActivity
import com.pg.gajamap.viewmodel.ClientViewModel

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@SettingFragment
        binding.fragment = this@SettingFragment
    }

    val positiveButtonClick = { dialogInterface: DialogInterface, i: Int ->
       viewModel.withdraw()
        viewModel.withdraw.observe(this, Observer {
            Toast.makeText(requireContext(), "탈퇴", Toast.LENGTH_SHORT).show()
            GajaMapApplication.prefs.clearAllPreferences()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        })
    }
    val negativeButtonClick = { dialogInterface: DialogInterface, i: Int ->
        Toast.makeText(requireContext(), "취소", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateAction() {

        val authority = GajaMapApplication.prefs.getString("authority", "")
        if(authority == "FREE"){
            binding.infoProfileImg.setBackgroundResource(R.drawable.setting_profile)
            binding.settingLevelTv.setText(R.string.setting_level)
            binding.settingLevelTv.resources.getColor(R.color.setting_level_yellow)
        }
        else if (authority == "VIP"){
            binding.infoProfileImg.setBackgroundResource(R.drawable.setting_profile_vip)
            binding.settingLevelTv.setText(R.string.setting_level_vip)
            binding.settingLevelTv.resources.getColor(R.color.setting_level_purple)
            binding.settingLevelTv.setBackgroundResource(R.color.setting_level_purple_background)
        }

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


        //로그아웃
        binding.settingLogoutTv.setOnClickListener {
            viewModel.logout()
            viewModel.logout.observe(this, Observer {
                GajaMapApplication.prefs.clearAllPreferences()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            })
        }

        //회원탈퇴
        binding.settingWithdrawTv.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("저장된 계정의 모든 정보가 삭제되며, 복구되지 않습니다.")
                .setPositiveButton("확인", positiveButtonClick)
                .setNegativeButton("취소", negativeButtonClick)
            val alertDialog = builder.create()
            alertDialog.show()

        }

    }
}