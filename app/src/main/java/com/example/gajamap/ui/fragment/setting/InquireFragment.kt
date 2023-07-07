package com.example.gajamap.ui.fragment.setting

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentInquireBinding
import com.example.gajamap.viewmodel.SettingViewModel

class InquireFragment: BaseFragment<FragmentInquireBinding>(R.layout.fragment_inquire) {

    override val viewModel by viewModels<SettingViewModel> {
        SettingViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@InquireFragment
        binding.fragment = this@InquireFragment
    }

    override fun onCreateAction() {

    }
}