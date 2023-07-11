package com.example.gajamap.ui.fragment.setting

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.gajamap.BR
import com.example.gajamap.R
import com.example.gajamap.base.BaseFragment
import com.example.gajamap.databinding.FragmentExcelBinding
import com.example.gajamap.viewmodel.ClientViewModel

class ExcelFragment : BaseFragment<FragmentExcelBinding>(R.layout.fragment_excel) {

    override val viewModel by viewModels<ClientViewModel> {
        ClientViewModel.SettingViewModelFactory("tmp")
    }

    override fun initViewModel(viewModel: ViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = this@ExcelFragment
        binding.fragment = this@ExcelFragment
    }

    override fun onCreateAction() {

    }

}