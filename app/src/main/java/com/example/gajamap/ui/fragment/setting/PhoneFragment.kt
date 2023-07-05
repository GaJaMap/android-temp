package com.example.gajamap.ui.fragment.setting

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentPhoneBinding
import com.example.gajamap.viewmodel.SettingViewModel

class PhoneFragment: BaseFragment<FragmentPhoneBinding>(R.layout.fragment_phone) {

    override val viewModel by viewModels<SettingViewModel> {
        SettingViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@PhoneFragment
        binding.fragment = this@PhoneFragment
    }

    override fun onCreateAction() {

        //스피너
        //스피너
        val itemList = listOf("그룹선택", "그룹 2", "그룹 3", "그룹 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, itemList)
        binding.settingPhoneSpinner.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        //리사이클러뷰
    }

}